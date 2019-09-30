
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IMsgClient = Class(nil, "ALittle.IMsgClient")

function IMsgClient:Send(msg_id, msg_body, rpc_id)
end

function IMsgClient:SendRPC(msg_id, msg_body)
end

function IMsgClient:Close(reason)
end

function IMsgClient.Invoke(msg_id, client, msg_body)
	client:Send(msg_id, msg_body, 0)
end

function IMsgClient.InvokeRPC(msg_id, client, msg_body)
	return client:SendRPC(msg_id, msg_body)
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

assert(IMsgClient, " extends class:IMsgClient is nil")
MsgCommon = Class(IMsgClient, "ALittle.MsgCommon")

function MsgCommon:Ctor()
	___rawset(self, "_invoke_map", {})
	___rawset(self, "_last_recv_time", 0)
	___rawset(self, "_id_creator", SafeIDCreator())
	___rawset(self, "_id_map_rpc", {})
end

function MsgCommon:IsConnected()
	return self._interface:IsConnected()
end

function MsgCommon:MessageRead(factory, msg_id)
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

function MsgCommon:MessageWrite(msg_id, msg_body)
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

function MsgCommon:HandleMessage(id, rpc_id, factory)
	if id == 0 then
		self._last_recv_time = os.clock()
		return
	end
	if rpc_id == 0 then
		local callback = FindMsgCallback(id)
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
		self:HandleRPCRequest(id, rpc_id, factory)
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

function MsgCommon:Send(msg_id, msg_body, rpc_id)
	if not self:IsConnected() then
		return
	end
	self._write_factory:SetRpcID(rpc_id)
	self:MessageWrite(msg_id, msg_body)
	self._interface:SendFactory(self._write_factory)
end

function MsgCommon:SendRpcError(rpc_id, reason)
	if not self:IsConnected() then
		return
	end
	self._write_factory:ResetOffset()
	self._write_factory:SetID(1)
	self._write_factory:SetRpcID(-rpc_id)
	self._write_factory:WriteString(reason)
	self._interface:SendFactory(self._write_factory)
end

function MsgCommon:SendRPC(msg_id, msg_body)
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
	self._interface:SendFactory(self._write_factory)
	local info = {}
	info.co = co
	info.rpc_id = rpc_id
	self._id_map_rpc[rpc_id] = info
	return ___coroutine.yield()
end

function MsgCommon:HandleRPCRequest(id, rpc_id, factory)
	local callback, return_id = FindMsgRpcCallback(id)
	if callback == nil then
		self:SendRpcError(rpc_id, "没有注册消息RPC回调函数")
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
		self:SendRpcError(rpc_id, return_body)
		Log("MsgSystem.HandleMessage callback invoke failed! by id:" .. id .. ", reason:" .. error)
		return
	end
	self:Send(return_id, return_body, -rpc_id)
end
MsgCommon.HandleRPCRequest = CoWrap(MsgCommon.HandleRPCRequest)

function MsgCommon:ClearRPC(reason)
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

