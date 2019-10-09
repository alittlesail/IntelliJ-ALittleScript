
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local concat = table.concat
local select = select
local tostring = tostring
LogLevel = {
	INFO = 0,
	WARN = 1,
	ERROR = 2,
}

function Log(...)
	local new_arg = {}
	local arg_count = select("#", ...)
	for i = 1, arg_count, 1 do
		Push(new_arg, tostring(select(i, ...)))
	end
	A_ScriptSystem:Log(concat(new_arg, "\t"), 0)
end

function Warn(...)
	local new_arg = {}
	local arg_count = select("#", ...)
	for i = 1, arg_count, 1 do
		Push(new_arg, tostring(select(i, ...)))
	end
	A_ScriptSystem:Log(concat(new_arg, "\t"), 1)
end

function Error(...)
	local new_arg = {}
	local arg_count = select("#", ...)
	for i = 1, arg_count, 1 do
		Push(new_arg, tostring(select(i, ...)))
	end
	A_ScriptSystem:Log(concat(new_arg, "\t"), 2)
end

