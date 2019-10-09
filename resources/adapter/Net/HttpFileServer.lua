
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local __all_callback = {}
Setweak(__all_callback, false, true)
function RegHttpFileCallback(method, callback)
	if __all_callback[method] ~= nil then
		Error("RegHttpFileCallback消息回调函数注册失败，名字为" .. method .. "已存在")
		return
	end
	__all_callback[method] = callback
end

function FindHttpFileCallback(method)
	return __all_callback[method]
end

IHttpFileServerInterface = Class(nil, "ALittle.IHttpFileServerInterface")

function IHttpFileServerInterface:Close(http_id)
end

function IHttpFileServerInterface:SendString(http_id, content)
end

function IHttpFileServerInterface:StartReceiveFile(http_id, file_path, start_size)
end

assert(ALittle.IHttpFileClient, " extends class:ALittle.IHttpFileClient is nil")
HttpFileServerTemplate = Class(ALittle.IHttpFileClient, "ALittle.HttpFileServerTemplate")

function HttpFileServerTemplate:Ctor(http_id, co)
	___rawset(self, "_http_id", http_id)
	___rawset(self, "_co", co)
	___rawset(self, "_invoked", false)
	___rawset(self, "_interface", self.__class.__element[1]())
end

function HttpFileServerTemplate.__getter:invoked()
	return self._invoked
end

function HttpFileServerTemplate:StartReceiveFile(file_path, start_size)
	if self._invoked then
		return "StartReceiveFile已经被调用过"
	end
	self._invoked = true
	self._interface:StartReceiveFile(self._http_id, file_path, start_size)
	return ___coroutine.yield()
end

function HttpFileServerTemplate:HandleReceiveResult(reason)
	local result, reason = coroutine.resume(self._co, reason)
	if not result then
		ALittle.Error(reason)
	end
end

function HttpFileServerTemplate:SendString(content)
	self._interface:SendString(self._http_id, content)
end

function HttpFileServerTemplate:Clsoe()
	self._interface:Close(self._http_id)
end

