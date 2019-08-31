
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local __object_index_function
__object_index_function = function(object, key)
	local clazz = rawget(object, "__class")
	local getter = clazz.__getter[key]
	if getter ~= nil then
		return getter(object)
	end
	local value = clazz[key]
	if value ~= nil then
		rawset(object, key, value)
		return value
	end
	return nil
end

local __object_newindex_function
__object_newindex_function = function(object, key, value)
	local clazz = rawget(object, "__class")
	local setter = clazz.__setter[key]
	if setter ~= nil then
		setter(object, value)
		return
	end
	rawset(object, key, value)
end

local __object_tostring_function
__object_tostring_function = function(object)
	local clazz = rawget(object, "__class")
	return "[ALittle Object instance of " .. tostring(clazz.__name) .. "]"
end

local __object_mt = {}
__object_mt.__index = __object_index_function
__object_mt.__newindex = __object_newindex_function
__object_mt.__tostring = __object_tostring_function
local __class_create_function
__class_create_function = function(clazz, object, ...)
	local super = rawget(clazz, "__super")
	if super ~= nil then
		__class_create_function(super, object, ...)
	end
	local ctor = rawget(clazz, "Ctor")
	if ctor ~= nil then
		ctor(object, ...)
	end
end

local __class_index_function
__class_index_function = function(clazz, key)
	local super = rawget(clazz, "__super")
	if super == nil then
		return nil
	end
	local value = super[key]
	rawset(clazz, key, value)
	return value
end

local __class_call_function
__class_call_function = function(clazz, ...)
	local object = {}
	object.__class = clazz
	setmetatable(object, __object_mt)
	__class_create_function(clazz, object, ...)
	return object
end

local __class_tostring_function
__class_tostring_function = function(clazz)
	return "[ALittle Class:" .. tostring(clazz.__name) .. "]"
end

local __class_mt = {}
__class_mt.__index = __class_index_function
__class_mt.__call = __class_call_function
__class_mt.__tostring = __class_tostring_function
function Class(super, name)
	local clazz = {}
	clazz.__super = super
	clazz.__name = name
	local setter = {}
	local getter = {}
	if super ~= nil then
		for k, v in ___pairs(super.__setter) do
			setter[k] = v
		end
		for k, v in ___pairs(super.__getter) do
			getter[k] = v
		end
	end
	clazz.__setter = setter
	clazz.__getter = getter
	setmetatable(clazz, __class_mt)
	return clazz
end

function Template(clazz, name, ...)
	local child = clazz.__child
	if child == nil then
		child = {}
		clazz.__child = child
	end
	local template = child[name]
	if template ~= nil then
		return template
	end
	template = {}
	child[name] = template
	template.__super = clazz.__super
	template.__name = name
	template.__setter = clazz.__setter
	template.__getter = clazz.__getter
	local map = {}
	local len = select("#", ...)
	for i = 1, len, 1 do
		local info = select(i, ...)
		map[info.__name] = info
	end
	template.__element = map
	setmetatable(template, __class_mt)
	return template
end

function GetClass(object)
	if type(object) ~= "table" then
		return nil
	end
	return object.__class
end

function NewObject(clazz, ...)
	return clazz(...)
end

