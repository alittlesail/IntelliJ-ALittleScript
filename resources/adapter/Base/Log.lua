
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

LogLevel = {
	INFO = 0,
	WARN = 1,
	ERROR = 2,
}

local __log = nil
function SetLogCallback(func)
	__log = func
end

local __push = table.push
local __concat = table.concat
function Log(...)
	local new_arg = {}
	local arg_count = select("#", ...)
	for i = 1, arg_count, 1 do
		__push(new_arg, tostring(select(i, ...)))
	end
	if __log == nil then
		print(__concat(new_arg, "\t"))
	else
		__log(__concat(new_arg, "\t"), 0)
	end
end

function Warn(...)
	local new_arg = {}
	local arg_count = select("#", ...)
	for i = 1, arg_count, 1 do
		__push(new_arg, tostring(select(i, ...)))
	end
	if __log == nil then
		print(__concat(new_arg, "\t"))
	else
		__log(__concat(new_arg, "\t"), 1)
	end
end

function Error(...)
	local new_arg = {}
	local arg_count = select("#", ...)
	for i = 1, arg_count, 1 do
		__push(new_arg, tostring(select(i, ...)))
	end
	if __log == nil then
		print(__concat(new_arg, "\t"))
	else
		__log(__concat(new_arg, "\t"), 2)
	end
end

