
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpInterface = Class(nil, "ALittle.IHttpInterface")

function IHttpInterface:GetID()
end

function IHttpInterface:SetURL(url, content)
end

function IHttpInterface:Start()
end

function IHttpInterface:Stop()
end

function IHttpInterface:GetResponse()
end

local __HttpClientMap = {}
function FindHttpClient(id)
	return __HttpClientMap[id]
end

assert(IHttpClient, " extends class:IHttpClient is nil")
HttpClientTemplate = Class(IHttpClient, "ALittle.HttpClientTemplate")

function HttpClientTemplate:Ctor(ip, port)
	___rawset(self, "_interface", self.__class.__element[1]())
	___rawset(self, "_ip", ip)
	___rawset(self, "_port", port)
end

function HttpClientTemplate:SendRPC(method, content)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程", nil
	end
	self._co = co
	__HttpClientMap[self._interface:GetID()] = self
	local url = "http://" .. self._ip .. ":" .. self._port .. "/" .. method
	if content == nil then
		self._interface:SetURL(url, nil)
	else
		self._interface:SetURL(url, json.encode(content))
	end
	self._interface:Start()
	return ___coroutine.yield()
end

function HttpClientTemplate:Stop()
	self._interface:Stop()
end

function HttpClientTemplate:HandleSucceed()
	__HttpClientMap[self._interface:GetID()] = nil
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

function HttpClientTemplate:HandleFailed(reason)
	__HttpClientMap[self._interface:GetID()] = nil
	local result, reason = coroutine.resume(self._co, reason, nil)
	if result ~= true then
		Error(reason)
	end
end

