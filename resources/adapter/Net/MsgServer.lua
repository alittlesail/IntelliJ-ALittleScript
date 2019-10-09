
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

assert(MsgCommon, " extends class:MsgCommon is nil")
MsgServer = Class(MsgCommon, "ALittle.MsgServer")

function MsgServer:Ctor(client_id, remote_ip, remote_port)
	___rawset(self, "_interface", self.__class.__element[1]())
	self._interface:SetID(client_id)
	___rawset(self, "_write_factory", lua.MessageWriteFactory())
	___rawset(self, "_is_connected", true)
	___rawset(self, "_client_id", client_id)
	___rawset(self, "_remote_ip", remote_ip)
	___rawset(self, "_remote_port", remote_port)
	___rawset(self, "_client_account_id", 0)
	___rawset(self, "_client_logining", false)
	___rawset(self, "_web_account_id", "")
	___rawset(self, "_web_is_logining", false)
end

function MsgServer.__getter:remote_ip()
	return self._remote_ip
end

function MsgServer.__getter:remote_port()
	return self._remote_port
end

function MsgServer:IsConnected()
	return self._is_connected
end

function MsgServer:HandleConnected()
	self._is_connected = true
end

function MsgServer:HandleDisconnected()
	self._is_connected = false
	self:ClearRPC("连接断开了")
end

function MsgServer:Close(reason)
	if not self._is_connected then
		return
	end
	self._is_connected = false
	if reason == nil then
		reason = "主动关闭连接"
	end
	self:ClearRPC(reason)
	self._interface:Close()
end

assert(MsgCommon, " extends class:MsgCommon is nil")
RouteServer = Class(MsgCommon, "ALittle.RouteServer")

function RouteServer:Ctor(connect_key, route_type, route_num)
	___rawset(self, "_interface", self.__class.__element[1]())
	self._interface:SetID(connect_key)
	___rawset(self, "_write_factory", lua.MessageWriteFactory())
	___rawset(self, "_is_connected", true)
	___rawset(self, "_connect_key", connect_key)
	___rawset(self, "_route_type", route_type)
	___rawset(self, "_route_num", route_num)
end

function RouteServer.__getter:route_type()
	return self._route_type
end

function RouteServer.__getter:route_num()
	return self._route_num
end

function RouteServer:IsConnected()
	return self._is_connected
end

function RouteServer:HandleConnected()
	self._is_connected = true
end

function RouteServer:HandleDisconnected()
	self._is_connected = false
	self:ClearRPC("连接断开了")
end

function RouteServer:Close(reason)
	if not self._is_connected then
		return
	end
	self._is_connected = false
	if reason == nil then
		reason = "主动关闭连接"
	end
	self:ClearRPC(reason)
	self._interface:Close()
end

