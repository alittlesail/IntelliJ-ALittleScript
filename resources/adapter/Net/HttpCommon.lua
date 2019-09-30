
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IHttpClient = Class(nil, "ALittle.IHttpClient")

function IHttpClient:SendRPC(method, content)
end

function IHttpClient.Invoke(method, client, content)
	return client:SendRPC(method, content)
end

