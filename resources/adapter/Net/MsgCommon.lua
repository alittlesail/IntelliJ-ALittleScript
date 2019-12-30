
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local __all_callback = {}
Setweak(__all_callback, false, true)
function RegMsgCallback(msg_id, callback)
	if __all_callback[msg_id] ~= nil then
		Error("RegMsgCallback消息回调函数注册失败，名字为" .. msg_id .. "已存在")
		return
	end
	__all_callback[msg_id] = callback
end

function FindMsgCallback(msg_id)
	return __all_callback[msg_id]
end

local __all_rpc_callback = {}
local __all_rpc_return_id = {}
Setweak(__all_rpc_callback, false, true)
function RegMsgRpcCallback(msg_id, callback, return_id)
	if __all_rpc_callback[msg_id] ~= nil then
		Error("RegMsgRpcCallback消息回调函数注册失败，名字为" .. msg_id .. "已存在")
		return
	end
	__all_rpc_callback[msg_id] = callback
	__all_rpc_return_id[msg_id] = return_id
end

function FindMsgRpcCallback(msg_id)
	return __all_rpc_callback[msg_id], __all_rpc_return_id[msg_id]
end

IMsgCommonNative = Class(nil, "ALittle.IMsgCommonNative")

function IMsgCommonNative:SetID(id)
end

function IMsgCommonNative:GetID()
	return 0
end

function IMsgCommonNative:Connect(ip, port)
end

function IMsgCommonNative:IsConnected()
	return false
end

function IMsgCommonNative:SendFactory(factory)
end

function IMsgCommonNative:Close()
end

IMsgCommon = Class(nil, "ALittle.IMsgCommon")

function IMsgCommon:Ctor()
	___rawset(self, "_invoke_map", {})
	___rawset(self, "_last_recv_time", 0)
	___rawset(self, "_id_creator", SafeIDCreator())
	___rawset(self, "_id_map_rpc", {})
end

function IMsgCommon:GetID()
	return self._interface:GetID()
end

function IMsgCommon:IsConnected()
	return self._interface:IsConnected()
end

function IMsgCommon:MessageRead(factory, msg_id)
	local info = self._invoke_map[msg_id]
	if info == nil then
		local error, invoke_info = TCall(CreateProtocolInvokeInfo, msg_id)
		if error ~= nil then
			Error(error)
			return nil
		end
		info = invoke_info
		self._invoke_map[msg_id] = info
	end
	return PS_ReadMessageForReceive(factory, info, factory:GetTotalSize())
end

function IMsgCommon:MessageWrite(msg_id, msg_body)
	local info = self._invoke_map[msg_id]
	if info == nil then
		local error, invoke_info = TCall(CreateProtocolInvokeInfo, msg_id)
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

function IMsgCommon:HandleMessage(id, rpc_id, factory)
	if id == 0 then
		self._last_recv_time = os.time(nil)
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
		local result, reason = coroutine.resume(info.co, factory:ReadString())
		if result ~= true then
			Error(reason)
		end
		return
	end
	local msg = self:MessageRead(factory, id)
	if msg == nil then
		local result, reason = coroutine.resume(info.co, "MsgSystem.HandleMessage MessageRead failed by id:" .. id)
		if result ~= true then
			Error(reason)
		end
		Log("MsgSystem.HandleMessage MessageRead failed by id:" .. id)
		return
	end
	local result, reason = coroutine.resume(info.co, nil, msg)
	if result ~= true then
		Error(reason)
	end
end

function IMsgCommon:SendMsg(T, msg)
	local info = T
	self:Send(info.hash_code, msg, 0)
end

function IMsgCommon:Send(msg_id, msg_body, rpc_id)
	if not self:IsConnected() then
		return
	end
	self._write_factory:SetRpcID(rpc_id)
	self:MessageWrite(msg_id, msg_body)
	self._interface:SendFactory(self._write_factory)
end

function IMsgCommon:SendRpcError(rpc_id, reason)
	if not self:IsConnected() then
		return
	end
	self._write_factory:ResetOffset()
	self._write_factory:SetID(1)
	self._write_factory:SetRpcID(-rpc_id)
	self._write_factory:WriteString(reason)
	self._interface:SendFactory(self._write_factory)
end

function IMsgCommon:SendRPC(msg_id, msg_body)
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

function IMsgCommon:HandleRPCRequest(id, rpc_id, factory)
	local callback, return_id = FindMsgRpcCallback(id)
	if callback == nil then
		self:SendRpcError(rpc_id, "没有注册消息RPC回调函数")
		Log("MsgSystem.HandleMessage can't find callback by id:" .. id)
		return
	end
	local msg = self:MessageRead(factory, id)
	if msg == nil then
		self:SendRpcError(rpc_id, "MsgSystem.HandleMessage MessageRead failed by id:" .. id)
		Log("MsgSystem.HandleMessage MessageRead failed by id:" .. id)
		return
	end
	local error, return_body = TCall(callback, self, msg)
	if error ~= nil then
		self:SendRpcError(rpc_id, error)
		Log("MsgSystem.HandleMessage callback invoke failed! by id:" .. id .. ", reason:" .. error)
		return
	end
	if return_body == nil then
		self:SendRpcError(rpc_id, "MsgSystem.HandleMessage callback have not return! by id:" .. id)
		Log("MsgSystem.HandleMessage callback have not return! by id:" .. id)
		return
	end
	self:Send(return_id, return_body, -rpc_id)
end
IMsgCommon.HandleRPCRequest = CoWrap(IMsgCommon.HandleRPCRequest)

function IMsgCommon:ClearRPC(reason)
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

function IMsgCommon:Close(reason)
	if reason == nil then
		reason = "主动关闭连接"
	end
	self:ClearRPC(reason)
	self._interface:Close()
end

function IMsgCommon.Invoke(msg_id, client, msg_body)
	client:Send(msg_id, msg_body, 0)
end

function IMsgCommon.InvokeRPC(msg_id, client, msg_body)
	return client:SendRPC(msg_id, msg_body)
end

