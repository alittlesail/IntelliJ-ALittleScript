
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpClient = Class(nil, "IHttpClient")

function IHttpClient:SendRPC(url, content)
end

IHttpInterface = Class(nil, "IHttpInterface")

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

HttpClient = Class(IHttpClient, "HttpClient")

function HttpClient:Ctor(interface)
	___rawset(self, "_interface", interface)
end

function HttpClient:SendRPC(url, content)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程", nil
	end
	self._co = co
	__HttpClientMap[self._interface:GetID()] = self
	if content == nil then
		self._interface:SetURL(url, nil)
	else
		self._interface:SetURL(url, Json.encode(content))
	end
	self._interface:Start()
	return ___coroutine.yield()
end

function HttpClient:Stop()
	self._interface:Stop()
end

function HttpClient:HandleSucceed()
	__HttpClientMap[self._interface:GetID()] = nil
	local error, param = pcall(Json.decode, self._interface:GetResponse())
	assert(coroutine.resume(self._co, error, param))
end

function HttpClient:HandleFailed(reason)
	__HttpClientMap[self._interface:GetID()] = nil
	assert(coroutine.resume(self._co, reason, nil))
end

