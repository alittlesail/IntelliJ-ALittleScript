
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IMessageWriteFactory = Class(nil, "ALittle.IMessageWriteFactory")

function IMessageWriteFactory:SetID(id)
end

function IMessageWriteFactory:SetRpcID(id)
end

function IMessageWriteFactory:ResetOffset()
end

function IMessageWriteFactory:GetOffset()
end

function IMessageWriteFactory:SetInt(offset, value)
end

function IMessageWriteFactory:WriteBool(value)
end

function IMessageWriteFactory:WriteInt(value)
end

function IMessageWriteFactory:WriteLongLong(value)
end

function IMessageWriteFactory:WriteString(value)
end

function IMessageWriteFactory:WriteDouble(value)
end

IMessageReadFactory = Class(nil, "ALittle.IMessageReadFactory")

function IMessageReadFactory:GetTotalSize()
end

function IMessageReadFactory:ReadBool()
end

function IMessageReadFactory:ReadInt()
end

function IMessageReadFactory:ReadLongLong()
end

function IMessageReadFactory:ReadString()
end

function IMessageReadFactory:ReadDouble()
end

function IMessageReadFactory:GetReadSize()
end

local PS_WriteBool
PS_WriteBool = function(factory, var_info, var_value)
	if var_value == nil then
		return factory:WriteBool(false)
	end
	return factory:WriteBool(var_value)
end

local PS_WriteInt
PS_WriteInt = function(factory, var_info, var_value)
	if var_value == nil then
		return factory:WriteInt(0)
	end
	return factory:WriteInt(var_value)
end

local PS_WriteString
PS_WriteString = function(factory, var_info, var_value)
	if var_value == nil then
		return factory:WriteString("")
	end
	return factory:WriteString(var_value)
end

local PS_WriteDouble
PS_WriteDouble = function(factory, var_info, var_value)
	if var_value == nil then
		return factory:WriteDouble(0)
	end
	return factory:WriteDouble(var_value)
end

local PS_WriteLongLong
PS_WriteLongLong = function(factory, var_info, var_value)
	if var_value == nil then
		return factory:WriteLongLong(0)
	end
	return factory:WriteLongLong(var_value)
end

local PS_WriteArray
PS_WriteArray = function(factory, var_info, var_value)
	if var_value == nil then
		return factory:WriteInt(0)
	end
	local offset = factory:GetOffset()
	local len = factory:WriteInt(0)
	local sub_info = var_info.sub_info
	local sub_func = sub_info.wfunc
	local count = 0
	for index, value in ___ipairs(var_value) do
		len = len + sub_func(factory, sub_info, value)
		count = count + 1
	end
	factory:SetInt(offset, count)
	return len
end

local PS_WriteMap
PS_WriteMap = function(factory, var_info, var_value)
	if var_value == nil then
		return factory:WriteInt(0)
	end
	local offset = factory:GetOffset()
	local len = factory:WriteInt(0)
	local key_info = var_info.key_info
	local key_func = key_info.wfunc
	local value_info = var_info.value_info
	local value_func = value_info.wfunc
	local count = 0
	for key, value in ___pairs(var_value) do
		len = len + key_func(factory, key_info, key)
		len = len + value_func(factory, value_info, value)
		count = count + 1
	end
	factory:SetInt(offset, count)
	return len
end

local PS_WriteMessage
PS_WriteMessage = function(factory, var_info, var_value)
	if var_value == nil then
		local offset = factory:GetOffset()
		local pre_size = factory:WriteInt(0)
		local size = 0
		for index, info in ___ipairs(var_info.handle) do
			size = size + info.wfunc(factory, info, nil)
		end
		factory:SetInt(offset, size)
		return pre_size + size
	end
	local offset = factory:GetOffset()
	local pre_size = factory:WriteInt(0)
	local size = 0
	for index, info in ___ipairs(var_info.handle) do
		size = size + info.wfunc(factory, info, var_value[info.var_name])
	end
	factory:SetInt(offset, size)
	return pre_size + size
end

function PS_WriteMessageForSend(factory, var_info, var_value)
	if var_value == nil then
		local offset = factory:GetOffset()
		local size = 0
		for index, info in ___ipairs(var_info.handle) do
			size = size + info.wfunc(factory, info, nil)
		end
		return size
	end
	local offset = factory:GetOffset()
	local size = 0
	for index, info in ___ipairs(var_info.handle) do
		size = size + info.wfunc(factory, info, var_value[info.var_name])
	end
	return size
end

local PS_ReadBool
PS_ReadBool = function(factory, var_info, len)
	if len == 0 then
		return false, 0
	end
	if len < 1 then
		return false, -1
	end
	return factory:ReadBool(), 1
end

local PS_ReadInt
PS_ReadInt = function(factory, var_info, len)
	if len == 0 then
		return 0, 0
	end
	if len < 4 then
		return 0, -1
	end
	return factory:ReadInt(), 4
end

local PS_ReadString
PS_ReadString = function(factory, var_info, len)
	if len == 0 then
		return "", 0
	end
	return factory:ReadString(), factory:GetReadSize()
end

local PS_ReadDouble
PS_ReadDouble = function(factory, var_info, len)
	if len == 0 then
		return 0, 0
	end
	if len < 8 then
		return 0, -1
	end
	return factory:ReadDouble(), 8
end

local PS_ReadLongLong
PS_ReadLongLong = function(factory, var_info, len)
	if len == 0 then
		return 0, 0
	end
	if len < 8 then
		return 0, -1
	end
	return factory:ReadLongLong(), 8
end

local PS_ReadArray
PS_ReadArray = function(factory, var_info, len)
	if len == 0 then
		return {}, 0
	end
	local save_len = len
	if len < 4 then
		return nil, -1
	end
	local count = factory:ReadInt()
	len = len - 4
	if count < 0 then
		return nil, -1
	end
	local sub_info = var_info.sub_info
	local sub_func = sub_info.rfunc
	local sub_len = 0
	local value_list = {}
	for index = 1, count, 1 do
		value_list[index], sub_len = sub_func(factory, sub_info, len)
		if sub_len < 0 then
			return nil, sub_len
		end
		len = len - (sub_len)
	end
	return value_list, save_len - len
end

local PS_ReadMap
PS_ReadMap = function(factory, var_info, len)
	if len == 0 then
		return {}, 0
	end
	local save_len = len
	if len < 4 then
		return nil, -1
	end
	local count = factory:ReadInt()
	len = len - 4
	if count < 0 then
		return nil, -1
	end
	local key_info = var_info.key_info
	local key_func = key_info.rfunc
	local value_info = var_info.value_info
	local value_func = value_info.rfunc
	local value_map = {}
	for index = 1, count, 1 do
		local key, key_len = key_func(factory, key_info, len)
		if key_len < 0 then
			return nil, key_len
		end
		len = len - key_len
		local value, value_len = value_func(factory, value_info, len)
		if value_len < 0 then
			return nil, value_len
		end
		len = len - value_len
		value_map[key] = value
	end
	return value_map, save_len - len
end

local PS_ReadMessage
PS_ReadMessage = function(factory, var_info, len)
	if len == 0 then
		local value_map = {}
		local sub_len = 0
		for index, info in ___ipairs(var_info.handle) do
			value_map[info.var_name], sub_len = info.rfunc(factory, info, 0)
		end
		return value_map, 0
	end
	if len < 4 then
		return nil, -1
	end
	local object_len = factory:ReadInt()
	len = len - 4
	local save_len = 4
	if object_len > len then
		return nil, -1
	end
	save_len = save_len + object_len
	local sub_len = 0
	local value_map = {}
	for index, info in ___ipairs(var_info.handle) do
		value_map[info.var_name], sub_len = info.rfunc(factory, info, object_len)
		if sub_len < 0 then
			return nil, sub_len
		end
		object_len = object_len - sub_len
	end
	return value_map, save_len
end

function PS_ReadMessageForReceive(factory, var_info, len)
	if len == 0 then
		local value_map = {}
		local sub_len = 0
		for index, info in ___ipairs(var_info.handle) do
			value_map[info.var_name], sub_len = info.rfunc(factory, info, 0)
		end
		return value_map, 0
	end
	local sub_len = 0
	local value_map = {}
	for index, info in ___ipairs(var_info.handle) do
		value_map[info.var_name], sub_len = info.rfunc(factory, info, len)
		if sub_len < 0 then
			return nil, sub_len
		end
		len = len - sub_len
	end
	return value_map, len
end

local __ps_write_data_map = {}
__ps_write_data_map["bool"] = PS_WriteBool
__ps_write_data_map["int"] = PS_WriteInt
__ps_write_data_map["I64"] = PS_WriteLongLong
__ps_write_data_map["string"] = PS_WriteString
__ps_write_data_map["double"] = PS_WriteDouble
local __ps_read_data_map = {}
__ps_read_data_map["bool"] = PS_ReadBool
__ps_read_data_map["int"] = PS_ReadInt
__ps_read_data_map["I64"] = PS_ReadLongLong
__ps_read_data_map["string"] = PS_ReadString
__ps_read_data_map["double"] = PS_ReadDouble
local __find = String.find
local __sub = String.sub
local __len = String.len
local __byte = String.byte
local __assert = assert
local CreateSubInfo
CreateSubInfo = function(sub_type)
	if __find(sub_type, "List", 1) == 1 then
		return CreateArrayInfo(sub_type)
	end
	if __find(sub_type, "Map", 1) == 1 then
		return CreateMapInfo(sub_type)
	end
	local wfunc = __ps_write_data_map[sub_type]
	if wfunc ~= nil then
		local sub_info = {}
		sub_info.wfunc = wfunc
		sub_info.rfunc = __ps_read_data_map[sub_type]
		return sub_info
	end
	return CreateMessageInfo(sub_type)
end

local CreateArrayInfo
CreateArrayInfo = function(var_type)
	local invoke_info = {}
	invoke_info.wfunc = PS_WriteArray
	invoke_info.rfunc = PS_ReadArray
	invoke_info.sub_info = CreateSubInfo(__sub(var_type, 6, -2))
	return invoke_info
end

local CreateMapInfo
CreateMapInfo = function(var_type)
	local invoke_info = {}
	local sub_type = __sub(var_type, 5, -2)
	local comma_index = 0
	local sub_type_len = __len(sub_type)
	local ltgt = 0
	for i = 1, sub_type_len, 1 do
		local code = __byte(sub_type, i)
		if code == 60 then
			ltgt = ltgt + 1
		elseif code == 62 then
			ltgt = ltgt - 1
		elseif code == 44 then
			if ltgt == 0 then
				comma_index = i
				break
			end
		end
	end
	assert(comma_index ~= 0, "can'f find comma in var_type:" .. var_type)
	invoke_info.wfunc = PS_WriteMap
	invoke_info.rfunc = PS_ReadMap
	invoke_info.key_info = CreateSubInfo(__sub(sub_type, 1, comma_index - 1))
	invoke_info.value_info = CreateSubInfo(__sub(sub_type, comma_index + 1))
	return invoke_info
end

local CreateMessageInfo
CreateMessageInfo = function(var_type)
	local reflect_info = FindReflectByName(var_type)
	__assert(reflect_info ~= nil, "FindReflect调用失败! 未知类型:" .. var_type)
	return CreateMessageInfoImpl(reflect_info)
end

local CreateMessageInfoImpl
CreateMessageInfoImpl = function(reflect_info)
	local invoke_info = {}
	invoke_info.wfunc = PS_WriteMessage
	invoke_info.rfunc = PS_ReadMessage
	local handle = {}
	invoke_info.handle = handle
	local handle_count = 0
	for index, var_name in ___ipairs(reflect_info.name_list) do
		local var_info = CreateSubInfo(reflect_info.type_list[index])
		var_info.var_name = var_name
		handle_count = handle_count + 1
		handle[handle_count] = var_info
	end
	return invoke_info
end

function CreateProtocolInvokeInfo(msg_id)
	local reflect_info = FindReflectById(msg_id)
	__assert(reflect_info ~= nil, "FindReflect调用失败! 未知ID:" .. msg_id)
	return CreateMessageInfoImpl(reflect_info)
end

