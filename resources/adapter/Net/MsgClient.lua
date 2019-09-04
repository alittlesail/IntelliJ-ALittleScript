
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IMsgClient = Class(nil, "ALittle.IMsgClient")

function IMsgClient:GetID()
end

function IMsgClient:Send(msg_id, msg_body, rpc_id)
end

function IMsgClient:SendRPC(msg_id, msg_body)
end

IMsgInterface = Class(nil, "ALittle.IMsgInterface")

function IMsgInterface:GetID()
end

function IMsgInterface:Connect(ip, port)
end

function IMsgInterface:IsConnected()
end

function IMsgInterface:SendFactory(factory)
end

function IMsgInterface:Close()
end

local __MsgClientMap = {}
function FindMsgClient(id)
	return __MsgClientMap[id]
end

assert(IMsgClient, " extends class:IMsgClient is nil")
MsgClient = Class(IMsgClient, "ALittle.MsgClient")

function MsgClient:Ctor(Msg_interface, factory, heartbeat, check_heartbeat)
	___rawset(self, "_msg_interface", self.__class.__element[1]())
	___rawset(self, "_write_factory", self.__class.__element[2]())
	___rawset(self, "_heartbeat", heartbeat)
	___rawset(self, "_heartbeat_loop", nil)
	___rawset(self, "_check_heartbeat", nil)
	if self._heartbeat ~= nil then
		___rawset(self, "_check_heartbeat", check_heartbeat)
	end
	___rawset(self, "_last_recv_time", 0)
	___rawset(self, "_id_creator", SafeIDCreator())
	___rawset(self, "_id_map_rpc", {})
	___rawset(self, "_connect_succeed_callback", nil)
	___rawset(self, "_connect_failed_callback", nil)
	___rawset(self, "_disconnected_callback", nil)
end

function MsgClient.__setter:connect_succeed_callback(value)
	self._connect_succeed_callback = value
end

function MsgClient.__setter:connect_failed_callback(value)
	self._connect_failed_callback = value
end

function MsgClient.__setter:disconnected_callback(value)
	self._disconnected_callback = value
end

function MsgClient:Connect(ip, port)
	self._msg_interface:Connect(ip, port)
	__MsgClientMap[self._msg_interface:GetID()] = self
end

function MsgClient:IsConnected()
	return self._msg_interface:IsConnected()
end

function MsgClient:HandleConnectSucceed()
	self._last_recv_time = 0
	self:SendHeartbeat()
	self:StartHeartbeat()
	if self._connect_succeed_callback ~= nil then
		self._connect_succeed_callback(self)
	end
end

function MsgClient:HandleDisconnect()
	self:StopHeartbeat()
	__MsgClientMap[self._msg_interface:GetID()] = nil
	self:ClearRPC("连接断开了")
	if self._disconnected_callback ~= nil then
		self._disconnected_callback(self)
	end
end

function MsgClient:HandleConnectFailed()
	__MsgClientMap[self._msg_interface:GetID()] = nil
	if self._connect_failed_callback ~= nil then
		self._connect_failed_callback(self)
	end
end

function MsgClient:MessageRead(factory, msg_id)
	local info = self._invoke_map[msg_id]
	if info == nil then
		local error, invoke_info = pcall(CreateProtocolInvokeInfo, msg_id)
		if error ~= nil then
			Error(error)
			return nil
		end
		info = invoke_info
		self._invoke_map[msg_id] = info
	end
	return PS_ReadMessageForReceive(factory, info, factory:GetTotalSize())
end

function MsgClient:MessageWrite(msg_id, msg_body)
	local info = self._invoke_map[msg_id]
	if info == nil then
		local error, invoke_info = pcall(CreateProtocolInvokeInfo, msg_id)
		if error ~= nil then
			Error(error)
			return
		end
		info = invoke_info
		self._invoke_map[msg_id] = info
	end
	self._write_factory:ResetOffset()
	PS_WriteMessageForSend(self._write_factory, info, msg_body)
	self._write_factory:SetID(msg_id)
end

function MsgClient:HandleMessage(id, rpc_id, factory)
	if id == 0 then
		self._last_recv_time = os.clock()
		return
	end
	if rpc_id == 0 then
		local callback = FindMessageCallback(id)
		if callback == nil then
			Log("MsgSystem.HandleMessage can't find callback by id:" .. id)
			return
		end
		local msg = self:MessageRead(factory, id)
		if msg == nil then
			Log("MsgSystem.HandleMessage MessageRead failed by id:" .. id)
			return
		end
		callback(self, msg)
		return
	end
	if rpc_id > 0 then
		local callback, return_id = FindMessageCallback(id)
		if callback == nil then
			self:SendRpcError(-rpc_id, "没有注册消息RPC回调函数")
			Log("MsgSystem.HandleMessage can't find callback by id:" .. id)
			return
		end
		local msg = self:MessageRead(factory, id)
		if msg == nil then
			Log("MsgSystem.HandleMessage MessageRead failed by id:" .. id)
			return
		end
		local error, return_body = pcall(callback, self, msg)
		if error ~= true then
			self:SendRpcError(-rpc_id, return_body)
			Log("MsgSystem.HandleMessage callback invoke failed! by id:" .. id .. ", reason:" .. error)
			return
		end
		self:Send(return_id, return_body, -rpc_id)
		return
	end
	rpc_id = -rpc_id
	self._id_creator:ReleaseID(rpc_id)
	local info = self._id_map_rpc[rpc_id]
	if info == nil then
		Log("MsgSystem.HandleMessage can't find rpc info by id:" .. id)
		return
	end
	self._id_map_rpc[rpc_id] = nil
	if id == 1 then
		coroutine.resume(info.co, factory:ReadString())
		return
	end
	local msg = self:MessageRead(factory, id)
	if msg == nil then
		Log("MsgSystem.HandleMessage MessageRead failed by id:" .. id)
		return
	end
	coroutine.resume(info.co, nil, msg)
end

function MsgClient:Send(msg_id, msg_body, rpc_id)
	self._write_factory:SetRpcID(rpc_id)
	self:MessageWrite(msg_id, msg_body)
	self._msg_interface:SendFactory(self._write_factory)
end

function MsgClient:Close(reason)
	self:StopHeartbeat()
	self._msg_interface:Close()
	if reason == nil then
		reason = "主动关闭连接"
	end
	self:ClearRPC(reason)
	__MsgClientMap[self._msg_interface:GetID()] = nil
end

function MsgClient:SendHeartbeat(max_ms)
	if self._msg_interface:IsConnected() == false then
		return
	end
	self._write_factory:ResetOffset()
	self._write_factory:SetID(0)
	self._write_factory:SetRpcID(0)
	self._msg_interface:SendFactory(self._write_factory)
	if self._check_heartbeat then
		local send_time = os.clock()
		local default_delta = self._heartbeat / 2
		local delta_time = max_ms
		if delta_time == nil then
			delta_time = default_delta
		end
		if delta_time > default_delta then
			delta_time = default_delta
		end
		local check_loop = LoopFunction(Bind(self.CheckHeartbeat, self, send_time, math.floor(delta_time), math.floor(delta_time)), 1, math.floor(delta_time), 1)
		check_loop:Start()
	end
end

function MsgClient:SendRpcError(rpc_id, reason)
	if self._msg_interface:IsConnected() == false then
		return
	end
	self._write_factory:ResetOffset()
	self._write_factory:SetID(1)
	self._write_factory:SetRpcID(rpc_id)
	self._write_factory:WriteString(reason)
	self._msg_interface:SendFactory(self._write_factory)
end

function MsgClient:CheckHeartbeat(send_time, cmp_time, delta_time)
	local invoke_time = os.time()
	local interval = invoke_time - send_time
	if delta_time > interval then
		delta_time = delta_time - interval
		local check_loop = LoopFunction(Bind(self.CheckHeartbeat, self, send_time * 1000, cmp_time, delta_time), 1, delta_time, 1)
		A_WeakLoopSystem:AddUpdater(check_loop)
		return
	end
	if self._last_recv_time > 0 and send_time - self._last_recv_time > cmp_time then
		if self._msg_interface:IsConnected() == false then
			return
		end
		self:Close("心跳检测失败，主动断开连接")
		if self._disconnected_callback ~= nil then
			self._disconnected_callback(self)
		end
	end
end

function MsgClient:SendRPC(msg_id, msg_body)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程", nil
	end
	if not self:IsConnected() then
		return "连接还没成功", nil
	end
	local rpc_id = self._id_creator:CreateID()
	self._write_factory:SetRpcID(rpc_id)
	self:MessageWrite(msg_id, msg_body)
	self._msg_interface:SendFactory(self._write_factory)
	local info = {}
	info.co = co
	info.rpc_id = rpc_id
	self._id_map_rpc[rpc_id] = info
	return ___coroutine.yield()
end

function MsgClient:HandleRPCMessage(rpc_id, error, param)
	local info = self._id_map_rpc[rpc_id]
	if info == nil then
		Error("出现未知的rpc_id.", rpc_id)
		return
	end
	self._id_creator:ReleaseID(rpc_id)
	self._id_map_rpc[rpc_id] = nil
	assert(coroutine.resume(info.co, error, param))
end

function MsgClient:ClearRPC(reason)
	local tmp = {}
	for rpc_id, info in ___pairs(self._id_map_rpc) do
		self._id_creator:ReleaseID(rpc_id)
		tmp[rpc_id] = info
	end
	self._id_map_rpc = {}
	for rpc_id, info in ___pairs(tmp) do
		local result, reason = coroutine.resume(info.co, reason, nil)
		if result ~= true then
			Error(reason)
		end
	end
end

function MsgClient:StartHeartbeat()
	if self._heartbeat == nil then
		return
	end
	if self._heartbeat <= 0 then
		return
	end
	if self._heartbeat_loop ~= nil then
		return
	end
	self._heartbeat_loop = LoopFunction(Bind(self.SendHeartbeat, self, nil), -1, self._heartbeat, 1)
	self._heartbeat_loop:Start()
end

function MsgClient:StopHeartbeat()
	if self._heartbeat_loop == nil then
		return
	end
	self._last_recv_time = 0
	self._heartbeat_loop:Close()
	self._heartbeat_loop = nil
end
