
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpClient = Class(nil, "ALittle.IHttpClient")

function IHttpClient:SendRPC(method, content)
end

function IHttpClient.Invoke(method, client, content)
	return client:SendRPC(method, content)
end

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
HttpClient = Class(IHttpClient, "ALittle.HttpClient")

function HttpClient:Ctor(ip, port)
	___rawset(self, "_interface", self.__class.__element[1]())
	___rawset(self, "_ip", ip)
	___rawset(self, "_port", port)
end

function HttpClient:SendRPC(method, content)
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

function HttpClient:Stop()
	self._interface:Stop()
end

function HttpClient:HandleSucceed()
	__HttpClientMap[self._interface:GetID()] = nil
	local error, param = pcall(json.decode, self._interface:GetResponse())
	if error ~= nil then
		assert(coroutine.resume(self._co, error, nil))
		return
	end
	if param["error"] ~= nil then
		assert(coroutine.resume(self._co, param["error"], nil))
		return
	end
	assert(coroutine.resume(self._co, nil, param))
end

function HttpClient:HandleFailed(reason)
	__HttpClientMap[self._interface:GetID()] = nil
	assert(coroutine.resume(self._co, reason, nil))
end

local __all_callback = {}
table.setweak(__all_callback, false, true)
function RegHttpCallback(method, callback)
	if __all_callback[method] ~= nil then
		Error("RegHttpCallback消息回调函数注册失败，名字为" .. method .. "已存在")
		return
	end
	__all_callback[method] = callback
end

function FindHttpCallback(method)
	return __all_callback[method]
end

local __all_download_callback = {}
table.setweak(__all_download_callback, false, true)
function RegHttpDownloadCallback(method, callback)
	if __all_download_callback[method] ~= nil then
		Error("RegHttpDownloadCallback消息回调函数注册失败，名字为" .. method .. "已存在")
		return
	end
	__all_download_callback[method] = callback
end

function FindHttpDownloadCallback(method)
	return __all_download_callback[method]
end

