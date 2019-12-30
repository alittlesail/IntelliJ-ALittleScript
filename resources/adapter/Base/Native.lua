
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

function Create()
	return {}
end

function Setweak(table, key, value)
	if not key and not value then
		setmetatable(table, nil)
		return
	end
	local mode = ""
	if key then
		mode = "k"
	end
	if value then
		mode = mode .. "v"
	end
	local mt = {}
	mt["__mode"] = mode
	setmetatable(table, mt)
end

function Push(list, object)
	table.insert(list, object)
end

function DateInfo(time)
	return os.date("*t", time)
end

function RandInt(min, max)
	return math.random(min, max)
end

function Find(content, substring)
	return string.find(content, substring, 1, true)
end

function TCall(...)
	local out_list = {pcall(...)}
	if out_list[1] ~= true then
		if out_list[2] == nil then
			return "nil"
		end
		return out_list[2]
	end
	local l = table.maxn(out_list)
	out_list[1] = nil
	return unpack(out_list, 1, l)
end

function Throw(error)
	A_ScriptSystem:Throw(error)
end

function Assert(value, error)
	if value ~= nil and value ~= false then
		return
	end
	if error == nil then
		error = "Assert failed"
	end
	A_ScriptSystem:Throw(error)
end

TimeSecond = {
	ONE_MINUTE_SECONDS = 60,
	ONE_HOUR_SECONDS = 3600,
	ONE_DAY_SECONDS = 86400,
	ONE_WEEK_DAY = 7,
	ONE_WEEK_SECONDS = 604800,
}

function GetNextTodayBeginTime()
	local date = DateInfo(nil)
	date.hour = 0
	date.min = 0
	date.sec = 0
	return os.time(date) + TimeSecond.ONE_DAY_SECONDS
end

local __VersionTime = 0
local __VersionIndex = 0
function NewTimeAndIndex()
	local cur_time = os.time(nil)
	if __VersionTime == cur_time then
		__VersionIndex = __VersionIndex + 1
	elseif cur_time > __VersionTime then
		__VersionTime = cur_time
		__VersionIndex = 0
	end
	return __VersionTime, __VersionIndex
end

