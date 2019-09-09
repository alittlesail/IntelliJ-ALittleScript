
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpFileClient = Class(nil, "ALittle.IHttpFileClient")

function IHttpFileClient:SendDownloadRPC(method, content)
end

function IHttpFileClient:SendUploadRPC(method, content)
end

function IHttpFileClient.InvokeDownload(method, client, content)
	return client:SendDownloadRPC(method, content)
end

function IHttpFileClient.InvokeUpload(method, client, content)
	return client:SendUploadRPC(method, content)
end

IHttpFileInterface = Class(nil, "ALittle.IHttpFileInterface")

function IHttpFileInterface:GetID()
end

function IHttpFileInterface:GetPath()
end

function IHttpFileInterface:SetURL(url, file_path, download, start_size)
end

function IHttpFileInterface:Start()
end

function IHttpFileInterface:Stop()
end

function IHttpFileInterface:GetCurrentSize()
end

function IHttpFileInterface:GetTotalSize()
end

local __HttpFileClientMap = {}
function FindHttpFileClient(id)
	return __HttpFileClientMap[id]
end

local __all_callback = {}
table.setweak(__all_callback, false, true)
function RegHttpFileCallback(method, callback)
	if __all_callback[method] ~= nil then
		Error("RegHttpCallback消息回调函数注册失败，名字为" .. method .. "已存在")
		return
	end
	__all_callback[method] = callback
end

function FindHttpFileCallback(method)
	return __all_callback[method]
end

assert(IHttpFileClient, " extends class:IHttpFileClient is nil")
HttpFileClient = Class(IHttpFileClient, "ALittle.HttpFileClient")

function HttpFileClient:Ctor(ip, port, file_path, start_size, callback)
	___rawset(self, "_interface", self.__class.__element[1]())
	___rawset(self, "_ip", ip)
	___rawset(self, "_port", port)
	___rawset(self, "_file_path", file_path)
	___rawset(self, "_start_size", start_size)
	___rawset(self, "_callback", callback)
end

function HttpFileClient:SendDownloadRPC(method, content)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程"
	end
	self._co = co
	__HttpFileClientMap[self._interface:GetID()] = self
	if self._start_size == nil then
		self._start_size = 0
	end
	local url = "http://" .. self._ip .. ":" .. self._port .. "/" .. method
	self._interface:SetURL(String_UrlAppendParamMap(url, content), self._file_path, true, self._start_size)
	self._interface:Start()
	return ___coroutine.yield()
end

function HttpFileClient:SendUploadRPC(method, content)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程", nil
	end
	self._co = co
	__HttpFileClientMap[self._interface:GetID()] = self
	if self._start_size == nil then
		self._start_size = 0
	end
	local url = "http://" .. self._ip .. ":" .. self._port .. "/" .. method
	self._interface:SetURL(String_UrlAppendParamMap(url, content), self._file_path, false, self._start_size)
	self._interface:Start()
	return ___coroutine.yield()
end

function HttpFileClient:Stop()
	self._interface:Stop()
end

function HttpFileClient:GetTotalSize()
	return self._interface:GetTotalSize()
end

function HttpFileClient:HandleSucceed()
	__HttpFileClientMap[self._interface:GetID()] = nil
	assert(coroutine.resume(self._co, nil, nil))
end

function HttpFileClient:HandleFailed(reason)
	__HttpFileClientMap[self._interface:GetID()] = nil
	assert(coroutine.resume(self._co, reason, nil))
end

function HttpFileClient:HandleProcess()
	if self._callback ~= nil then
		self._callback(self._interface)
	end
end

