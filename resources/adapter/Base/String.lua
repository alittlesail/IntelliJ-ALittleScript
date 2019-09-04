
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local __lshift = bit.lshift
local __rshift = bit.rshift
local __bxor = bit.bxor
local __byte = String.byte
local __sub = String.sub
local __len = String.len
local __find = String.find
local __concat = table.concat
function JSHash(str)
	local l = __len(str)
	local h = l
	local step = __rshift(l, 5) + 1
	for i = l, step, -step do
		h = __bxor(h, (__lshift(h, 5) + __byte(str, i) + __rshift(h, 2)))
	end
	return h
end

function String_CopyTable(info)
	local new_info = {}
	for key, value in ___pairs(info) do
		if type(value) ~= "table" then
			new_info[key] = value
		else
			new_info[key] = String_CopyTable(value)
		end
	end
	return new_info
end

function String_Split(target, sep)
	if target == nil then
		return {}
	end
	if sep == nil or sep == "" then
		sep = ":"
	end
	local fields = {}
	local fields_count = 0
	local start_pos = 1
	while true do
		local start_index = __find(target, sep, start_pos)
		if start_index == nil then
			fields_count = fields_count + 1
			fields[fields_count] = __sub(target, start_pos)
			break
		end
		fields_count = fields_count + 1
		fields[fields_count] = __sub(target, start_pos, start_index - 1)
		start_pos = start_index + __len(sep)
	end
	return fields
end

function String_SplitSepList(target, sep_list)
	if target == nil then
		return {}
	end
	if sep_list == nil then
		sep_list = {}
	end
	local fields = {}
	local fields_count = 0
	local start_pos = 1
	while true do
		local start_index
		local end_index
		for _, sep in ___ipairs(sep_list) do
			local start_index_tmp = __find(target, sep, start_pos)
			if start_index_tmp ~= nil then
				if start_index == nil or start_index_tmp < start_index then
					start_index = start_index_tmp
					end_index = start_index + __len(sep) - 1
				end
			end
		end
		if start_index == nil then
			local value = __sub(target, start_pos)
			if __len(value) > 0 then
				fields_count = fields_count + 1
				fields[fields_count] = __sub(target, start_pos)
			end
			break
		end
		local value = __sub(target, start_pos, start_index - 1)
		if __len(value) > 0 then
			fields_count = fields_count + 1
			fields[fields_count] = __sub(target, start_pos, start_index - 1)
		end
		start_pos = end_index + 1
	end
	return fields
end

function String_Join(list, sep)
	return __concat(list, sep)
end

function String_UrlAppendParam(url, param)
	if String.find(url, "?", 1) == nil then
		url = url .. "?"
	else
		url = url .. "&"
	end
	return url .. param
end

function String_UrlAnalyse(url)
	local info = {}
	info.value_map = {}
	local start_pos = 1
	local start_index = String.find(url, "http://", start_pos)
	if start_index ~= nil then
		info.protocol = "http"
		start_pos = start_index + String.len("http://")
	else
		start_index = String.find(url, "https://", start_pos)
		if start_index ~= nil then
			info.protocol = "https"
			start_pos = start_index + String.len("https://")
		end
	end
	local ip_and_port = nil
	start_index = String.find(url, "/", start_pos)
	if start_index ~= nil then
		ip_and_port = String.sub(url, start_pos, start_index - 1)
	else
		ip_and_port = String.sub(url, start_pos)
	end
	local ip_start = String.find(ip_and_port, ":", 1)
	if ip_start ~= nil then
		info.ip = String.sub(ip_and_port, 1, ip_start - 1)
		info.port = math.floor(tonumber(String.sub(ip_and_port, ip_start + 1)))
	else
		info.ip = ip_and_port
		info.port = 80
		if info.protocol == "https" then
			info.port = 443
		end
	end
	if start_index == nil then
		return info
	end
	start_pos = start_index
	start_index = String.find(url, "?", start_pos)
	if start_index ~= nil then
		info.path = String.sub(url, start_pos, start_index - 1)
	else
		info.path = String.sub(url, start_pos)
	end
	if start_index == nil then
		return info
	end
	start_pos = start_index + 1
	local param_list = String_Split(String.sub(url, start_pos), "&")
	for k, v in ___ipairs(param_list) do
		local param_list_list = String_Split(v, "=")
		if param_list_list[1] ~= nil and param_list_list[2] ~= nil then
			info.value_map[param_list_list[1]] = param_list_list[2]
		end
	end
	return info
end

StringGenerateID = Class(nil, "ALittle.StringGenerateID")

function StringGenerateID:Ctor()
	math.randomseed(os.time())
	___rawset(self, "_string_last_index", 0)
	___rawset(self, "_string_last_time", 0)
end

function StringGenerateID:GenID(pre)
	local cur_time = os.time()
	if cur_time ~= self._string_last_time then
		self._string_last_time = cur_time
		self._string_last_index = 0
	else
		self._string_last_index = self._string_last_index + 1
	end
	local result = ""
	if pre ~= nil then
		result = pre .. "-"
	end
	result = result .. self._string_last_time .. "-" .. self._string_last_index .. "-" .. math.random(0, 10000) .. "-" .. math.random(0, 10000)
	return result
end

local A_StringGenerateID = StringGenerateID()
function String_GenerateID(pre)
	return A_StringGenerateID:GenID(pre)
end

