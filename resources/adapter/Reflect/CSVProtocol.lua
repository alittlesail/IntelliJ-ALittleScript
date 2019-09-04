
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

ICSVFile = Class(nil, "ALittle.ICSVFile")

function ICSVFile:Close()
end

function ICSVFile:ReadCell(lua_row, lua_col)
end

function ICSVFile:GetRowCount()
end

function ICSVFile:GetColCount()
end

local CSV_ReadBool
CSV_ReadBool = function(content, value)
	return content == "true"
end

local CSV_ReadInt
CSV_ReadInt = function(content, value)
	if content == "" then
		return 0
	end
	return math.floor(tonumber(content))
end

local CSV_ReadLongLong
CSV_ReadLongLong = function(content, value)
	if content == "" then
		return 0
	end
	return math.floor(tonumber(content))
end

local CSV_ReadString
CSV_ReadString = function(content, value)
	return content
end

local CSV_ReadDouble
CSV_ReadDouble = function(content, value)
	if content == "" then
		return 0
	end
	return tonumber(content)
end

local CSV_ReadArray
CSV_ReadArray = function(content, value)
	local list = String_Split(content, value.split)
	local result = {}
	for index, sub in ___ipairs(list) do
		local v = value.func(sub, value.sub_info)
		if v == nil then
			return nil
		end
		result[index] = v
	end
	return result
end

function CSV_ReadMessage(content, value)
	local list = String_Split(content, value.split)
	local t = table.create()
	for index, handle in ___ipairs(value.handle) do
		t[handle.var_name] = handle.func(list[index], handle)
	end
	return t
end

local __csv_read_data_map = {}
__csv_read_data_map["bool"] = CSV_ReadBool
__csv_read_data_map["int"] = CSV_ReadInt
__csv_read_data_map["I64"] = CSV_ReadLongLong
__csv_read_data_map["string"] = CSV_ReadString
__csv_read_data_map["double"] = CSV_ReadDouble
__split_list = {"#", "*", "|"}
__split_list_last = __split_list[table.maxn(__split_list)]
local __find = String.find
local __sub = String.sub
local __len = String.len
local __byte = String.byte
local __assert = assert
local CreateSubInfo
CreateSubInfo = function(sub_type, split_index)
	if __find(sub_type, "List", 1) == 1 then
		return CreateArrayInfo(sub_type, split_index)
	end
	if __find(sub_type, "Map", 1) == 1 then
		__assert(false, "不支持Map解析")
	end
	local func = __csv_read_data_map[sub_type]
	if func ~= nil then
		local sub_info = {}
		sub_info.func = func
		return sub_info
	end
	return CreateMessageInfo(sub_type, split_index)
end

local CreateArrayInfo
CreateArrayInfo = function(var_type, split_index)
	__assert(split_index > 0, "分隔符数量不足")
	local invoke_info = {}
	invoke_info.func = CSV_ReadArray
	invoke_info.split = __split_list[split_index]
	invoke_info.sub_info = CreateSubInfo(__sub(var_type, 6, -2), split_index - 1)
	return invoke_info
end

local CreateMessageInfo
CreateMessageInfo = function(var_type, split_index)
	__assert(split_index > 0, "分隔符数量不足")
	local reflect_info = FindReflectByName(var_type)
	__assert(reflect_info ~= nil, "FindReflectByName调用失败! 未知类型:" .. var_type)
	local invoke_info = {}
	invoke_info.split = __split_list[split_index]
	invoke_info.func = CSV_ReadMessage
	local handle = {}
	invoke_info.handle = handle
	local handle_count = 0
	for index, var_name in ___ipairs(reflect_info.name_list) do
		local var_info = CreateSubInfo(reflect_info.type_list[index], split_index - 1)
		var_info.var_name = var_name
		handle_count = handle_count + 1
		handle[handle_count] = var_info
	end
	return invoke_info
end

function CreateCSVInfo(reflect_info)
	local split_index = table.maxn(__split_list)
	__assert(split_index > 0, "分隔符数量不足")
	local invoke_info = {}
	invoke_info.split = __split_list[split_index]
	invoke_info.func = CSV_ReadMessage
	local handle = {}
	invoke_info.handle = handle
	local handle_count = 0
	for index, var_name in ___ipairs(reflect_info.name_list) do
		local var_info = CreateSubInfo(reflect_info.type_list[index], split_index - 1)
		var_info.var_name = var_name
		handle_count = handle_count + 1
		handle[handle_count] = var_info
	end
	return invoke_info
end

