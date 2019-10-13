
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

CSVConfig = Class(nil, "ALittle.CSVConfig")

function CSVConfig:Init(loader)
	self._csv_file = loader
end

function CSVConfig:GetFieldInfo(field)
	local field_index = 0
	local is_number = false
	for index, name in ___ipairs(self._reflect_info.name_list) do
		if name == field then
			field_index = index
			local var_type = self._reflect_info.type_list[index]
			is_number = var_type == "int" or var_type == "double" or var_type == "I64"
			break
		end
	end
	return field_index, is_number
end

function CSVConfig:GetFieldNameList()
	return self._reflect_info.name_list
end

function CSVConfig:ReadCell(lua_row, lua_col)
	return self._csv_file:ReadCell(lua_row, lua_col)
end

function CSVConfig:LoadCell(row)
	local value = {}
	for index, handle in ___ipairs(self._csv_info.handle) do
		value[handle.var_name] = handle.func(self._csv_file:ReadCell(row, index), handle)
	end
	return value
end

KeyValueConfig = Class(nil, "ALittle.KeyValueConfig")

function KeyValueConfig:Ctor()
	___rawset(self, "_data", {})
end

function KeyValueConfig:Init(loader)
	self._data = {}
	if loader == nil then
		return
	end
	local row_count = loader:GetRowCount()
	for row = 1, row_count, 1 do
		local key = loader:ReadCell(row, 1)
		local value = loader:ReadCell(row, 2)
		if key ~= nil and value ~= nil then
			self._data[key] = value
		end
	end
	loader:Close()
end

function KeyValueConfig:GetString(key, default)
	local value = self._data[key]
	if value == nil then
		return default
	end
	return value
end

function KeyValueConfig:GetInt(key, default)
	local value = self._data[key]
	if value == nil then
		return default
	end
	if value == "" then
		return 0
	end
	return math.floor(tonumber(value))
end

function KeyValueConfig:GetDouble(key, default)
	local value = self._data[key]
	if value == nil then
		return default
	end
	if value == "" then
		return 0
	end
	return tonumber(value)
end

function KeyValueConfig:GetIntList(key, default)
	local value = self._data[key]
	if value == nil then
		return default
	end
	local list = String_Split(value, __split_list_last)
	local int_list = {}
	for index, v in ___ipairs(list) do
		if v == "" then
			int_list[index] = 0
		else
			int_list[index] = math.floor(tonumber(v))
		end
	end
	return int_list
end

function KeyValueConfig:GetDoubleList(key, default)
	local value = self._data[key]
	if value == nil then
		return default
	end
	local list = String_Split(value, __split_list_last)
	local double_list = {}
	for index, v in ___ipairs(list) do
		if v == "" then
			double_list[index] = 0
		else
			double_list[index] = tonumber(v)
		end
	end
	return double_list
end

function KeyValueConfig:GetStringList(key, default)
	local value = self._data[key]
	if value == nil then
		return default
	end
	return String_Split(value, __split_list_last)
end

assert(CSVConfig, " extends class:CSVConfig is nil")
SingleKeyTableConfig = Class(CSVConfig, "ALittle.SingleKeyTableConfig")

function SingleKeyTableConfig:Ctor()
	___rawset(self, "_reflect_info", self.__class.__element[1])
	___rawset(self, "_key_map", {})
	___rawset(self, "_cache_map", {})
	Setweak(self._cache_map, false, true)
end

function SingleKeyTableConfig.__getter:key_map()
	return self._key_map
end

function SingleKeyTableConfig:Init(loader)
	if self._csv_file ~= nil then
		self._csv_file:Close()
	end
	self._csv_file = loader
	if self._csv_file == nil then
		return
	end
	local key_type = self._reflect_info.name_list[1]
	local is_number = key_type == "int" or key_type == "double" or key_type == "I64"
	local row_count = self._csv_file:GetRowCount()
	for row = 1, row_count, 1 do
		local value = self._csv_file:ReadCell(row, 1)
		if value ~= nil then
			if is_number then
				self._key_map[tonumber(value)] = row
			else
				self._key_map[value] = row
			end
		end
	end
end

function SingleKeyTableConfig:GetData(key)
	if self._csv_file == nil then
		return nil
	end
	local value = self._cache_map[key]
	if value ~= nil then
		return value
	end
	local row = self._key_map[key]
	if row == nil then
		return nil
	end
	value = self:LoadCell(row)
	if value == nil then
		return nil
	end
	self._cache_map[key] = value
	return value
end

function SingleKeyTableConfig:CreateIndex(field)
	if self._csv_file == nil then
		return nil
	end
	local col_index, is_number = self:GetFieldInfo(field)
	if col_index == 0 then
		return nil
	end
	return Template(SingleKeyTableIndexConfig, "ALittle.SingleKeyTableIndexConfig<"..self.__class.__element[1].name..">", self.__class.__element[1])(self, col_index, is_number)
end

SingleKeyTableIndexConfig = Class(nil, "ALittle.SingleKeyTableIndexConfig")

function SingleKeyTableIndexConfig:Ctor(parent, col_index, is_number)
	___rawset(self, "_parent", parent)
	___rawset(self, "_value_map", {})
	for key, row in ___pairs(self._parent.key_map) do
		local value
		if is_number then
			value = tonumber(self._parent:ReadCell(row, col_index))
		else
			value = self._parent:ReadCell(row, col_index)
		end
		local key_set = self._value_map[value]
		if key_set == nil then
			key_set = {}
			self._value_map[value] = key_set
		end
		key_set[key] = true
	end
end

function SingleKeyTableIndexConfig:GetKeySet(key)
	return self._value_map[key]
end

function SingleKeyTableIndexConfig:GetOne(key)
	local keys = self._value_map[key]
	if keys == nil then
		return nil
	end
	for k, _ in ___pairs(keys) do
		return self._parent:GetData(k)
	end
	return nil
end

function SingleKeyTableIndexConfig:GetList(key)
	local list = {}
	local keys = self._value_map[key]
	if keys == nil then
		return list
	end
	local count = 0
	for k, _ in ___pairs(keys) do
		count = count + 1
		list[count] = self._parent:GetData(k)
	end
	return list
end

assert(CSVConfig, " extends class:CSVConfig is nil")
DoubleKeyTableConfig = Class(CSVConfig, "ALittle.DoubleKeyTableConfig")

function DoubleKeyTableConfig:Ctor()
	___rawset(self, "_reflect_info", self.__class.__element[1])
	___rawset(self, "_key_map", {})
	___rawset(self, "_cache_map", {})
	Setweak(self._cache_map, false, true)
end

function DoubleKeyTableConfig:Init(loader)
	if self._csv_file ~= nil then
		self._csv_file:Close()
	end
	self._csv_file = loader
	if self._csv_file == nil then
		return
	end
	local first_key_type = self._reflect_info.name_list[1]
	local first_is_number = first_key_type == "int" or first_key_type == "double" or first_key_type == "I64"
	local second_key_type = self._reflect_info.name_list[2]
	local second_is_number = second_key_type == "int" or second_key_type == "double" or second_key_type == "I64"
	local row_count = self._csv_file:GetRowCount()
	for row = 1, row_count, 1 do
		local tmp
		local value = self._csv_file:ReadCell(row, 1)
		if value ~= nil then
			if first_is_number then
				tmp = self._key_map[tonumber(value)]
				if tmp == nil then
					tmp = {}
					self._key_map[tonumber(value)] = tmp
				end
			else
				tmp = self._key_map[value]
				if tmp == nil then
					tmp = {}
					self._key_map[value] = tmp
				end
			end
		end
		value = self._csv_file:ReadCell(row, 2)
		if value ~= nil then
			if second_is_number then
				tmp[tonumber(value)] = row
			else
				tmp[value] = row
			end
		end
	end
end

function DoubleKeyTableConfig:GetData(first_key, second_key)
	if self._csv_file == nil then
		return nil
	end
	local value = self._cache_map[first_key]
	if value ~= nil then
		value = value[second_key]
		if value ~= nil then
			return value
		end
	end
	local key_tmp = self._key_map[first_key]
	if key_tmp == nil then
		return nil
	end
	local row = key_tmp[second_key]
	if row == nil then
		return nil
	end
	value = self:LoadCell(row)
	if value == nil then
		return nil
	end
	local tmp = self._cache_map[first_key]
	if tmp == nil then
		tmp = {}
		self._cache_map[first_key] = tmp
	end
	tmp[second_key] = value
	return value
end

