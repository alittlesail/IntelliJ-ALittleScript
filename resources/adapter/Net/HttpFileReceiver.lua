
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpFileReceiverNative = Class(nil, "ALittle.IHttpFileReceiverNative")

function IHttpFileReceiverNative:Close(http_id)
end

function IHttpFileReceiverNative:SendString(http_id, content)
end

function IHttpFileReceiverNative:StartReceiveFile(http_id, file_path, start_size)
end

IHttpFileReceiver = Class(nil, "ALittle.IHttpFileReceiver")

function IHttpFileReceiver:StartReceiveFile(file_path, start_size)
	return "not impl"
end

local __all_callback = {}
Setweak(__all_callback, false, true)
function RegHttpFileCallback(method, callback)
	if __all_callback[method] ~= nil then
		Error("RegHttpFileCallback消息回调函数注册失败，名字为" .. method .. "已存在")
		return
	end
	__all_callback[method] = callback
end

function FindHttpFileReceiverCallback(method)
	return __all_callback[method]
end

assert(IHttpFileReceiver, " extends class:IHttpFileReceiver is nil")
HttpFileReceiverTemplate = Class(IHttpFileReceiver, "ALittle.HttpFileReceiverTemplate")

function HttpFileReceiverTemplate:Ctor(http_id, co)
	___rawset(self, "_http_id", http_id)
	___rawset(self, "_co", co)
	___rawset(self, "_invoked", false)
	___rawset(self, "_interface", self.__class.__element[1]())
end

function HttpFileReceiverTemplate.__getter:invoked()
	return self._invoked
end

function HttpFileReceiverTemplate:StartReceiveFile(file_path, start_size)
	if self._invoked then
		return "StartReceiveFile已经被调用过"
	end
	self._invoked = true
	self._interface:StartReceiveFile(self._http_id, file_path, start_size)
	return ___coroutine.yield()
end

function HttpFileReceiverTemplate:HandleReceiveResult(reason)
	local result, reason = coroutine.resume(self._co, reason)
	if not result then
		Error(reason)
	end
end

function HttpFileReceiverTemplate:SendString(content)
	self._interface:SendString(self._http_id, content)
end

function HttpFileReceiverTemplate:Clsoe()
	self._interface:Close(self._http_id)
end

