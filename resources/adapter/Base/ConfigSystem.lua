
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

ConfigSystem = Class(nil, "ConfigSystem")

function ConfigSystem:Ctor(file_path)
	___rawset(self, "_file_path", file_path)
	___rawset(self, "_config_map", {})
	local file = self:CreateFileLoader()
	if file == nil then
		Log("CreateFileLoader failed!")
		return
	end
	local content = file:Load(self._file_path)
	if content == nil then
		return
	end
	local error, json_content = pcall(Json.decode, content)
	if error ~= nil then
		Log("Json Decode failed." .. file_path .. ", " .. error)
		return
	end
	___rawset(self, "_config_map", json_content)
end

function ConfigSystem:CreateFileLoader()
	return NormalFileLoader()
end

function ConfigSystem:CreateFileSaver()
	return NormalFileSaver()
end

function ConfigSystem:LoadFile(file)
	local file = io.open(file, "r")
	if file == nil then
		return nil
	end
	local content = file:read("*a")
	file:close()
end

function ConfigSystem:GetConfig(key, default)
	local value = self._config_map[key]
	if value == nil then
		return default
	end
	return value
end

function ConfigSystem:SetConfig(key, value, not_save)
	self._config_map[key] = value
	if not not_save then
		self:SaveConfig()
	end
end

function ConfigSystem:CoverConfig(msg, save)
	for k, v in ___pairs(msg) do
		self._config_map[k] = v
	end
	if not save then
		self:SaveConfig()
	end
end

function ConfigSystem:SaveConfig()
	local file = self:CreateFileSaver()
	if file == nil then
		Log("CreateFileSaver failed!")
		return
	end
	if not file:Save(self._file_path, Json.encode(self._config_map)) then
		Log("Save Congig Failed.", self._file_path)
	end
end

