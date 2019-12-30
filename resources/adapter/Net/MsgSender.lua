
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local __MsgSenderMap = {}
function FindMsgSender(id)
	return __MsgSenderMap[id]
end

assert(IMsgCommon, " extends class:IMsgCommon is nil")
MsgSenderTemplate = Class(IMsgCommon, "ALittle.MsgSenderTemplate")

function MsgSenderTemplate:Ctor(heartbeat, check_heartbeat, callback)
	___rawset(self, "_interface", self.__class.__element[1]())
	___rawset(self, "_write_factory", MessageWriteFactory())
	___rawset(self, "_heartbeat", heartbeat)
	___rawset(self, "_heartbeat_loop", nil)
	___rawset(self, "_check_heartbeat", nil)
	if self._heartbeat ~= nil then
		___rawset(self, "_check_heartbeat", check_heartbeat)
	end
	___rawset(self, "_last_recv_time", 0)
	___rawset(self, "_callback", callback)
end

function MsgSenderTemplate:Connect(ip, port)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程"
	end
	self._co = co
	__MsgSenderMap[self._interface:GetID()] = self
	self._ip = ip
	self._port = port
	self._interface:Connect(ip, port)
	return ___coroutine.yield()
end

function MsgSenderTemplate:HandleConnectSucceed()
	self._last_recv_time = 0
	self:SendHeartbeat(nil)
	self:StartHeartbeat()
	local result, reason = coroutine.resume(self._co, nil)
	if result ~= true then
		Error(reason)
	end
end

function MsgSenderTemplate:HandleDisconnect()
	self:StopHeartbeat()
	__MsgSenderMap[self._interface:GetID()] = nil
	self:ClearRPC("连接断开了")
	if self._callback ~= nil then
		self._callback()
	end
end

function MsgSenderTemplate:HandleConnectFailed(error)
	__MsgSenderMap[self._interface:GetID()] = nil
	if error == nil then
		error = self._ip .. ":" .. self._port .. "连接失败"
	end
	local result, reason = coroutine.resume(self._co, error)
	if result ~= true then
		Error(reason)
	end
end

function MsgSenderTemplate:Close(reason)
	self:StopHeartbeat()
	self._interface:Close()
	if reason == nil then
		reason = "主动关闭连接"
	end
	self:ClearRPC(reason)
	__MsgSenderMap[self._interface:GetID()] = nil
end

function MsgSenderTemplate:SendHeartbeat(max_ms)
	if self._interface:IsConnected() == false then
		return
	end
	self._write_factory:ResetOffset()
	self._write_factory:SetID(0)
	self._write_factory:SetRpcID(0)
	self._interface:SendFactory(self._write_factory)
	if self._check_heartbeat then
		local send_time = os.time(nil)
		local default_delta = self._heartbeat / 2
		local delta_time = max_ms
		if delta_time == nil then
			delta_time = default_delta
		end
		if delta_time > default_delta then
			delta_time = default_delta
		end
		A_LoopSystem:AddTimer(math.floor(delta_time) * 1000, Bind(self.CheckHeartbeat, self, send_time, math.floor(delta_time)), nil, nil)
	end
end

function MsgSenderTemplate:CheckHeartbeat(send_time, delta_time)
	local invoke_time = os.time(nil)
	local interval_time = invoke_time - send_time
	if interval_time > delta_time + 2 then
		return
	end
	if self._last_recv_time > 0 and send_time - self._last_recv_time > delta_time then
		if self._interface:IsConnected() == false then
			return
		end
		self:Close("心跳检测失败，主动断开连接")
		if self._callback ~= nil then
			self._callback()
		end
	end
end

function MsgSenderTemplate:StartHeartbeat()
	if self._heartbeat == nil then
		return
	end
	if self._heartbeat <= 0 then
		return
	end
	if self._heartbeat_loop ~= nil then
		return
	end
	self._heartbeat_loop = A_LoopSystem:AddTimer(1, Bind(self.SendHeartbeat, self, nil), -1, self._heartbeat * 1000)
end

function MsgSenderTemplate:StopHeartbeat()
	if self._heartbeat_loop == nil then
		return
	end
	self._last_recv_time = 0
	A_LoopSystem:RemoveTimer(self._heartbeat_loop)
	self._heartbeat_loop = nil
end

