
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

local floor = math.floor
local tonumber = tonumber
local maxn = table.maxn
local CSV_ReadBool
CSV_ReadBool = function(content, value)
	return content == "true"
end

local CSV_ReadInt
CSV_ReadInt = function(content, value)
	if content == "" then
		return 0
	end
	return floor(tonumber(content))
end

local CSV_ReadLongLong
CSV_ReadLongLong = function(content, value)
	if content == "" then
		return 0
	end
	return floor(tonumber(content))
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
	local t = {}
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
__split_list_last = __split_list[maxn(__split_list)]
local find = Find
local sub = string.sub
function CreateCSVSubInfo(sub_type, split_index)
	if find(sub_type, "List", 1) == 1 then
		return CreateCSVArrayInfo(sub_type, split_index)
	end
	if find(sub_type, "Map", 1) == 1 then
		Assert(false, "不支持Map解析")
	end
	local func = __csv_read_data_map[sub_type]
	if func ~= nil then
		local sub_info = {}
		sub_info.func = func
		return sub_info
	end
	return CreateCSVInfoImpl(sub_type, split_index)
end

function CreateCSVArrayInfo(var_type, split_index)
	Assert(split_index > 0, "分隔符数量不足")
	local invoke_info = {}
	invoke_info.func = CSV_ReadArray
	invoke_info.split = __split_list[split_index]
	invoke_info.sub_info = CreateCSVSubInfo(sub(var_type, 6, -2), split_index - 1)
	return invoke_info
end

function CreateCSVInfoImpl(var_type, split_index)
	Assert(split_index > 0, "分隔符数量不足")
	local reflect_info = FindStructByName(var_type)
	Assert(reflect_info ~= nil, "FindReflectByName调用失败! 未知类型:" .. var_type)
	local invoke_info = {}
	invoke_info.split = __split_list[split_index]
	invoke_info.func = CSV_ReadMessage
	local handle = {}
	invoke_info.handle = handle
	local handle_count = 0
	for index, var_name in ___ipairs(reflect_info.name_list) do
		local var_info = CreateCSVSubInfo(reflect_info.type_list[index], split_index - 1)
		var_info.var_name = var_name
		handle_count = handle_count + 1
		handle[handle_count] = var_info
	end
	return invoke_info
end

function CreateCSVInfo(reflect_info)
	local split_index = maxn(__split_list)
	Assert(split_index > 0, "分隔符数量不足")
	local invoke_info = {}
	invoke_info.split = __split_list[split_index]
	invoke_info.func = CSV_ReadMessage
	local handle = {}
	invoke_info.handle = handle
	local handle_count = 0
	for index, var_name in ___ipairs(reflect_info.name_list) do
		local var_info = CreateCSVSubInfo(reflect_info.type_list[index], split_index - 1)
		var_info.var_name = var_name
		handle_count = handle_count + 1
		handle[handle_count] = var_info
	end
	return invoke_info
end

