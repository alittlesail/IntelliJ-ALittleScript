
module("ALittleScript", package.seeall)

bit = bit
coroutine = coroutine
require = require
local __assert = assert
assert = function(condition, message)
    __assert(condition, message or "")
end
type = type
collectgarbage = collectgarbage
local __pcall = pcall
local __unpack = unpack
__pcall = function(...)
    local out_list = {pcall(...)}
    if out_list[1] ~= true then return out_list[2] end
    return unpack(out_list, 2)
end
unpack = unpack
tostring = tostring
tonumber = tonumber
package = package
next = next
math = math

local __os = os
os = {}
os.clock = __os.clock
os.date = function(value, time)
    if (value == "*t") return nil end
    return __os.date(value, time)
end
os.dateinfo = function(time)
    return __os.date("*t", time)
end
os.difftime = __os.difftime
os.execute = __os.execute
os.exit = __os.exit
os.remove = __os.remove
os.rename = __os.rename
os.time = __os.time

local __find = string.find
String = {}
String.byte = string.byte
String.char = string.char
String.len = string.len
String.lower = string.lower
String.upper = string.upper
String.sub = string.sub
String.lower = string.lower
String.find = function(content, substring, init)
    return __find(content, substring, init, true)
end

setmetatable = setmetatable

local __table = table
table = {}
table.concat = __table.concat
table.insert = __table.insert
table.push = __table.insert
table.maxn = __table.maxn
table.remove = __table.remove
table.sort = __table.sort
table.create = function()
    return {}
end
-- 设置弱引用
table.setweak = function(object, key, value)
    local mode = ""
    if key then mode = "k" end
    if value then mode = mode.."v" end
    setmetatable(object, { __mode = mode })
end

select = select
rawget = rawget
rawset = rawset
print = print