
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local __MsgClientMap = {}
function FindMsgClient(id)
	return __MsgClientMap[id]
end

assert(MsgCommon, " extends class:MsgCommon is nil")
MsgClient = Class(MsgCommon, "ALittle.MsgClient")

function MsgClient:Ctor(heartbeat, check_heartbeat, callback)
	___rawset(self, "_interface", self.__class.__element[1]())
	___rawset(self, "_write_factory", self.__class.__element[2]())
	___rawset(self, "_heartbeat", heartbeat)
	___rawset(self, "_heartbeat_loop", nil)
	___rawset(self, "_check_heartbeat", nil)
	if self._heartbeat ~= nil then
		___rawset(self, "_check_heartbeat", check_heartbeat)
	end
	___rawset(self, "_last_recv_time", 0)
	___rawset(self, "_callback", callback)
end

function MsgClient:Connect(ip, port)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程"
	end
	self._co = co
	__MsgClientMap[self._interface:GetID()] = self
	self._ip = ip
	self._port = port
	self._interface:Connect(ip, port)
	return ___coroutine.yield()
end

function MsgClient:HandleConnectSucceed()
	self._last_recv_time = 0
	self:SendHeartbeat()
	self:StartHeartbeat()
	__MsgClientMap[self._interface:GetID()] = nil
	assert(coroutine.resume(self._co, nil))
end

function MsgClient:HandleDisconnect()
	self:StopHeartbeat()
	__MsgClientMap[self._interface:GetID()] = nil
	self:ClearRPC("连接断开了")
	if self._callback ~= nil then
		self._callback()
	end
end

function MsgClient:HandleConnectFailed(error)
	__MsgClientMap[self._interface:GetID()] = nil
	if error == nil then
		error = self._ip .. ":" .. self._port .. "连接失败"
	end
	assert(coroutine.resume(self._co, error))
end

function MsgClient:Close(reason)
	self:StopHeartbeat()
	self._interface:Close()
	if reason == nil then
		reason = "主动关闭连接"
	end
	self:ClearRPC(reason)
	__MsgClientMap[self._interface:GetID()] = nil
end

function MsgClient:SendHeartbeat(max_ms)
	if self._interface:IsConnected() == false then
		return
	end
	self._write_factory:ResetOffset()
	self._write_factory:SetID(0)
	self._write_factory:SetRpcID(0)
	self._interface:SendFactory(self._write_factory)
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
		if self._interface:IsConnected() == false then
			return
		end
		self:Close("心跳检测失败，主动断开连接")
		if self._callback ~= nil then
			self._callback()
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

