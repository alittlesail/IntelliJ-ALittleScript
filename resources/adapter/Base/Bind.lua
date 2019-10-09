
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local select = select
local unpack = unpack
local tostring = tostring
local setmetatable = setmetatable
local coroutine = coroutine
local __functor_mt = {}
local __functor_mt__call
__functor_mt__call = function(caller, ...)
	local new_arg_list = {}
	local arg_list = caller._arg
	local arg_count = caller._arg_count
	for i = 1, arg_count, 1 do
		new_arg_list[i] = arg_list[i]
	end
	local add_count = select("#", ...)
	for i = 1, add_count, 1 do
		new_arg_list[arg_count + i] = select(i, ...)
	end
	return caller._func(unpack(new_arg_list, 1, arg_count + add_count))
end

__functor_mt.__call = __functor_mt__call
local __functor_mt__tostring
__functor_mt__tostring = function(caller)
	return "[ALittle Functor:" .. tostring(caller) .. "]"
end

__functor_mt.__tostring = __functor_mt__tostring
function Bind(func, ...)
	if select("#", ...) == 0 then
		return func
	end
	local object = {}
	object._func = func
	object._arg = {...}
	object._arg_count = select("#", ...)
	setmetatable(object, __functor_mt)
	return object
end

local __co_functor_mt = {}
local __co_functor_mt__call
__co_functor_mt__call = function(caller, ...)
	return coroutine.wrap(caller._func)(...)
end

__co_functor_mt.__call = __co_functor_mt__call
local __co_functor_mt__tostring
__co_functor_mt__tostring = function(caller)
	return "[ALittle CoWrap:" .. tostring(caller) .. "]"
end

__co_functor_mt.__tostring = __co_functor_mt__tostring
function CoWrap(func)
	local object = {}
	object._func = func
	setmetatable(object, __co_functor_mt)
	return object
end

