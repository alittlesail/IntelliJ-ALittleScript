
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local byte = string.byte
local sub = string.sub
local len = string.len
local find = Find
local concat = table.concat
local type = type
local tostring = tostring
local tonumber = tonumber
local floor = math.floor
local time = os.time
local maxn = table.maxn
local random = math.random
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

function String_Trim(content)
	return A_ScriptSystem:Trim(content)
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
		local start_index = find(target, sep, start_pos)
		if start_index == nil then
			fields_count = fields_count + 1
			fields[fields_count] = sub(target, start_pos)
			break
		end
		fields_count = fields_count + 1
		fields[fields_count] = sub(target, start_pos, start_index - 1)
		start_pos = start_index + len(sep)
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
			local start_index_tmp = find(target, sep, start_pos)
			if start_index_tmp ~= nil then
				if start_index == nil or start_index_tmp < start_index then
					start_index = start_index_tmp
					end_index = start_index + len(sep) - 1
				end
			end
		end
		if start_index == nil then
			local value = sub(target, start_pos)
			if len(value) > 0 then
				fields_count = fields_count + 1
				fields[fields_count] = sub(target, start_pos)
			end
			break
		end
		local value = sub(target, start_pos, start_index - 1)
		if len(value) > 0 then
			fields_count = fields_count + 1
			fields[fields_count] = sub(target, start_pos, start_index - 1)
		end
		start_pos = end_index + 1
	end
	return fields
end

function String_Join(list, sep)
	return concat(list, sep)
end

function String_UrlAppendParam(url, param)
	if find(url, "?", 1) == nil then
		url = url .. "?"
	else
		url = url .. "&"
	end
	return url .. param
end

function String_UrlAppendParamMap(url, param)
	local list = {}
	local count = 0
	for key, value in ___pairs(param) do
		count = count + 1
		list[count] = key .. "=" .. tostring(value)
	end
	if find(url, "?", 1) == nil then
		url = url .. "?"
	else
		url = url .. "&"
	end
	return url .. String_Join(list, "&")
end

function String_UrlAnalyse(url)
	local info = {}
	info.value_map = {}
	local start_pos = 1
	local start_index = find(url, "http://", start_pos)
	if start_index ~= nil then
		info.protocol = "http"
		start_pos = start_index + len("http://")
	else
		start_index = find(url, "https://", start_pos)
		if start_index ~= nil then
			info.protocol = "https"
			start_pos = start_index + len("https://")
		end
	end
	local ip_and_port = nil
	start_index = find(url, "/", start_pos)
	if start_index ~= nil then
		ip_and_port = sub(url, start_pos, start_index - 1)
	else
		ip_and_port = sub(url, start_pos)
	end
	local ip_start = find(ip_and_port, ":", 1)
	if ip_start ~= nil then
		info.ip = sub(ip_and_port, 1, ip_start - 1)
		info.port = floor(tonumber(sub(ip_and_port, ip_start + 1)))
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
	start_index = find(url, "?", start_pos)
	if start_index ~= nil then
		info.path = sub(url, start_pos, start_index - 1)
	else
		info.path = sub(url, start_pos)
	end
	if start_index == nil then
		return info
	end
	start_pos = start_index + 1
	local param_list = String_Split(sub(url, start_pos), "&")
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
	___rawset(self, "_string_last_index", 0)
	___rawset(self, "_string_last_time", 0)
end

function StringGenerateID:GenID(pre)
	local cur_time = time()
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
	result = result .. self._string_last_time .. "-" .. self._string_last_index .. "-" .. random(0, 10000) .. "-" .. random(0, 10000)
	return result
end

local A_StringGenerateID = StringGenerateID()
function String_GenerateID(pre)
	return A_StringGenerateID:GenID(pre)
end

function String_Replace(content, old_split, new_split)
	local list = String_Split(content, old_split)
	return String_Join(list, new_split)
end

function String_IsPhoneNumber(number)
	local l = len(number)
	for i = 1, l, 1 do
		local value = byte(number, i)
		if value < 48 then
			return false
		end
		if value > 57 then
			return false
		end
	end
	return true
end

function String_HttpAnalysisValueMap(param, content)
	local value_map = {}
	local param_split_list = String_Split(param, "&")
	for index, param_content in ___ipairs(param_split_list) do
		local value_split_list = String_Split(param_content, "=")
		if maxn(value_split_list) == 2 then
			if sub(value_split_list[2], 1, 1) == "\"" and sub(value_split_list[2], -1, -1) == "\"" then
				value_map[value_split_list[1]] = sub(value_split_list[2], 2, -2)
			else
				local number = tonumber(value_split_list[2])
				if number == nil then
					value_map[value_split_list[1]] = value_split_list[2]
				else
					value_map[value_split_list[1]] = number
				end
			end
		end
	end
	if len(content) > 0 then
		local error, value = TCall(json.decode, content)
		if error == nil then
			for k, v in ___pairs(value) do
				value_map[k] = v
			end
		end
	end
	return value_map
end

function String_SplitUTF8(content, out)
	local result = {}
	local l = len(content)
	local offset = 0
	local count = 0
	while l > offset do
		local byte_count = A_ScriptSystem:GetUTF8ByteCountByOffset(content, offset)
		count = count + 1
		result[count] = sub(content, offset + 1, offset + byte_count)
		offset = offset + byte_count
	end
	out.list = result
	out.count = count
end

function String_MD5(content)
	return A_ScriptSystem:StringMD5(content)
end

function String_FileMD5(path)
	return A_ScriptSystem:FileMD5(path)
end

function String_Sha1(content)
	return A_ScriptSystem:Sha1(content)
end

function String_Base64Decode(content)
	return A_ScriptSystem:Base64Decode(content)
end

function String_Base64Encode(content)
	return A_ScriptSystem:Base64Encode(content)
end

function String_UTF8ToGBK(content)
	return A_ScriptSystem:UTF82GBK(content)
end

function String_GBKToUTF8(content)
	return A_ScriptSystem:GBK2UTF8(content)
end

function String_GetUTF8Length(content)
	return A_ScriptSystem:GetUTF8Length(content)
end

function String_CalcUTF8LengthOfWord(content, word_count)
	return A_ScriptSystem:CalcUTF8LengthOfWord(content, word_count)
end

function String_FormatTime2Remain(remain_time)
	local room_time_desc = ""
	if remain_time > TimeSecond.ONE_DAY_SECONDS then
		local count = math.floor(remain_time / TimeSecond.ONE_DAY_SECONDS)
		remain_time = remain_time - count * TimeSecond.ONE_DAY_SECONDS
		room_time_desc = room_time_desc .. count .. "天"
	end
	if remain_time > TimeSecond.ONE_HOUR_SECONDS then
		local count = math.floor(remain_time / TimeSecond.ONE_HOUR_SECONDS)
		remain_time = remain_time - count * TimeSecond.ONE_HOUR_SECONDS
		room_time_desc = room_time_desc .. count .. "小时"
	end
	if remain_time > TimeSecond.ONE_MINUTE_SECONDS then
		local count = math.floor(remain_time / TimeSecond.ONE_MINUTE_SECONDS)
		remain_time = remain_time - count * TimeSecond.ONE_MINUTE_SECONDS
		room_time_desc = room_time_desc .. count .. "分钟"
	end
	if remain_time > 0 then
		local count = math.floor(remain_time / TimeSecond.ONE_MINUTE_SECONDS)
		remain_time = remain_time - count * TimeSecond.ONE_MINUTE_SECONDS
		room_time_desc = room_time_desc .. remain_time .. "秒"
	end
	return room_time_desc
end

