
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpFileClient = Class(nil, "ALittle.IHttpFileClient")

function IHttpFileClient:SendRPC(url, file_path, download, start_size)
end

IHttpFileInterface = Class(nil, "ALittle.IHttpFileInterface")

function IHttpFileInterface:GetID()
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

assert(IHttpFileClient, " extends class:IHttpFileClient is nil")
HttpFileClient = Class(IHttpFileClient, "ALittle.HttpFileClient")

function HttpFileClient:Ctor(callback)
	___rawset(self, "_interface", self.__class.__element[1]())
	___rawset(self, "_callback", callback)
end

function HttpFileClient:SendRPC(url, file_path, download, start_size)
	local co = coroutine.running()
	if co == nil then
		return "当前不是协程", nil
	end
	self._co = co
	__HttpFileClientMap[self._interface:GetID()] = self
	if start_size == nil then
		start_size = 0
	end
	self._interface:SetURL(url, file_path, download, start_size)
	self._interface:Start()
	return ___coroutine.yield()
end

function HttpFileClient:Stop()
	self._interface:Stop()
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

