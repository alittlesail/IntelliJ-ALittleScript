
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local __all_name_reflect = {}
local __all_id_reflect = {}
function RegReflect(hash, name, info)
	if __all_name_reflect[name] ~= nil then
		return
	end
	local old_info = __all_id_reflect[hash]
	if old_info ~= nil then
		Error("RegReflect 名字为" .. name .. "和名字为" .. old_info.name .. "哈希值冲突")
		return
	end
	__all_name_reflect[name] = info
	__all_id_reflect[hash] = info
end

function FindReflectByName(name)
	return __all_name_reflect[name]
end

function FindReflectById(id)
	return __all_id_reflect[id]
end

