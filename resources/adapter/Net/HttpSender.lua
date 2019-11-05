
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpSenderNative = Class(nil, "ALittle.IHttpSenderNative")

function IHttpSenderNative:GetID()
	return 0
end

function IHttpSenderNative:SetURL(url, content)
end

function IHttpSenderNative:Start()
end

function IHttpSenderNative:Stop()
end

function IHttpSenderNative:GetResponse()
	return nil
end

IHttpSender = Class(nil, "ALittle.IHttpSender")

function IHttpSender:SendRPC(method, content)
	return "not impl", nil
end

function IHttpSender.Invoke(method, client, content)
	return client:SendRPC(method, content)
end

local __HttpSenderMap = {}
function FindHttpSender(id)
	return __HttpSenderMap[id]
end

assert(IHttpSender, " extends class:IHttpSender is nil")
HttpSenderTemplate = Class(IHttpSender, "ALittle.HttpSenderTemplate")

function HttpSenderTemplate:Ctor(ip, port)
	___rawset(self, "_interface", self.__class.__element[1]())
	___rawset(self, "_ip", ip)
	___rawset(self, "_port", port)
end

function HttpSenderTemplate:SendRPC(method, content)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程", nil
	end
	self._co = co
	__HttpSenderMap[self._interface:GetID()] = self
	local url = "http://" .. self._ip .. ":" .. self._port .. "/" .. method
	if content == nil then
		self._interface:SetURL(url, nil)
	else
		self._interface:SetURL(url, json.encode(content))
	end
	self._interface:Start()
	return ___coroutine.yield()
end

function HttpSenderTemplate:Stop()
	self._interface:Stop()
end

function HttpSenderTemplate:HandleSucceed()
	__HttpSenderMap[self._interface:GetID()] = nil
	local error, param = TCall(json.decode, self._interface:GetResponse())
	if error ~= nil then
		local result, reason = coroutine.resume(self._co, error, nil)
		if result ~= true then
			Error(reason)
		end
		return
	end
	if param["error"] ~= nil then
		local result, reason = coroutine.resume(self._co, param["error"], nil)
		if result ~= true then
			Error(reason)
		end
		return
	end
	local result, reason = coroutine.resume(self._co, nil, param)
	if result ~= true then
		Error(reason)
	end
end

function HttpSenderTemplate:HandleFailed(reason)
	__HttpSenderMap[self._interface:GetID()] = nil
	local result, reason = coroutine.resume(self._co, reason, nil)
	if result ~= true then
		Error(reason)
	end
end

