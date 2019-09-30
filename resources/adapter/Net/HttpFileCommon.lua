
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpFileClient = Class(nil, "ALittle.IHttpFileClient")

function IHttpFileClient:SendDownloadRPC(method, content)
end

function IHttpFileClient:SendUploadRPC(method, content)
end

function IHttpFileClient:StartReceiveFile(file_path, start_size)
end

function IHttpFileClient.InvokeDownload(method, client, content)
	return client:SendDownloadRPC(method, content)
end

function IHttpFileClient.InvokeUpload(method, client, content)
	return client:SendUploadRPC(method, content)
end

