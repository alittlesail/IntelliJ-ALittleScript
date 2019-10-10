
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpFileSenderNative = Class(nil, "ALittle.IHttpFileSenderNative")

function IHttpFileSenderNative:GetID()
end

function IHttpFileSenderNative:SetURL(url, file_path, download, start_size)
end

function IHttpFileSenderNative:Start()
end

function IHttpFileSenderNative:Stop()
end

function IHttpFileSenderNative:GetPath()
end

function IHttpFileSenderNative:GetCurrentSize()
end

function IHttpFileSenderNative:GetTotalSize()
end

IHttpFileSender = Class(nil, "ALittle.IHttpFileSender")

function IHttpFileSender:SendDownloadRPC(method, content)
end

function IHttpFileSender:SendUploadRPC(method, content)
end

function IHttpFileSender.InvokeDownload(method, client, content)
	return client:SendDownloadRPC(method, content)
end

function IHttpFileSender.InvokeUpload(method, client, content)
	return client:SendUploadRPC(method, content)
end

local __HttpFileSenderMap = {}
function FindHttpFileSender(id)
	return __HttpFileSenderMap[id]
end

assert(IHttpFileSender, " extends class:IHttpFileSender is nil")
HttpFileSenderTemplate = Class(IHttpFileSender, "ALittle.HttpFileSenderTemplate")

function HttpFileSenderTemplate:Ctor(ip, port, file_path, start_size, callback)
	___rawset(self, "_interface", self.__class.__element[1]())
	___rawset(self, "_ip", ip)
	___rawset(self, "_port", port)
	___rawset(self, "_file_path", file_path)
	___rawset(self, "_start_size", start_size)
	___rawset(self, "_callback", callback)
end

function HttpFileSenderTemplate:SendDownloadRPC(method, content)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程"
	end
	self._co = co
	__HttpFileSenderMap[self._interface:GetID()] = self
	if self._start_size == nil then
		self._start_size = 0
	end
	local url = "http://" .. self._ip .. ":" .. self._port .. "/" .. method
	self._interface:SetURL(String_UrlAppendParamMap(url, content), self._file_path, true, self._start_size)
	self._interface:Start()
	return ___coroutine.yield()
end

function HttpFileSenderTemplate:SendUploadRPC(method, content)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程", nil
	end
	self._co = co
	__HttpFileSenderMap[self._interface:GetID()] = self
	if self._start_size == nil then
		self._start_size = 0
	end
	local url = "http://" .. self._ip .. ":" .. self._port .. "/" .. method
	self._interface:SetURL(String_UrlAppendParamMap(url, content), self._file_path, false, self._start_size)
	self._interface:Start()
	return ___coroutine.yield()
end

function HttpFileSenderTemplate:Stop()
	self._interface:Stop()
end

function HttpFileSenderTemplate:GetTotalSize()
	return self._interface:GetTotalSize()
end

function HttpFileSenderTemplate:HandleSucceed()
	__HttpFileSenderMap[self._interface:GetID()] = nil
	local result, reason = coroutine.resume(self._co, nil, nil)
	if result ~= true then
		Error(reason)
	end
end

function HttpFileSenderTemplate:HandleFailed(reason)
	__HttpFileSenderMap[self._interface:GetID()] = nil
	local result, reason = coroutine.resume(self._co, reason, nil)
	if result ~= true then
		Error(reason)
	end
end

function HttpFileSenderTemplate:HandleProcess()
	if self._callback ~= nil then
		self._callback(self._interface)
	end
end

