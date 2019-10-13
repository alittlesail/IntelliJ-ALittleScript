
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local __all_name_struct = {}
local __all_id_struct = {}
function RegStruct(hash, name, info)
	if __all_name_struct[name] ~= nil then
		return
	end
	local old_info = __all_id_struct[hash]
	if old_info ~= nil then
		Error("RegReflect 名字为" .. name .. "和名字为" .. old_info.name .. "哈希值冲突")
		return
	end
	__all_name_struct[name] = info
	__all_id_struct[hash] = info
end

function FindStructByName(name)
	return __all_name_struct[name]
end

function FindStructById(id)
	return __all_id_struct[id]
end

