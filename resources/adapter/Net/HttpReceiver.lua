
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpReceiver = Class(nil, "ALittle.IHttpReceiver")

local __all_callback = {}
Setweak(__all_callback, false, true)
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
Setweak(__all_download_callback, false, true)
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

IHttpReceiverNative = Class(nil, "ALittle.IHttpReceiverNative")

function IHttpReceiverNative:Close(http_id)
end

function IHttpReceiverNative:SendString(http_id, content)
end

function IHttpReceiverNative:SendFile(http_id, file_path, start_size)
end

assert(IHttpReceiver, " extends class:IHttpReceiver is nil")
HttpReceiverTemplate = Class(IHttpReceiver, "ALittle.HttpReceiverTemplate")

function HttpReceiverTemplate:Ctor(http_id)
	___rawset(self, "_http_id", http_id)
	___rawset(self, "_interface", self.__class.__element[1]())
end

function HttpReceiverTemplate:Close()
	self._interface:Close(self._http_id)
end

function HttpReceiverTemplate:SendString(content)
	self._interface:SendString(self._http_id, content)
end

function HttpReceiverTemplate:SendFile(file_path, start_size)
	self._interface:SendFile(self._http_id, file_path, start_size)
end

