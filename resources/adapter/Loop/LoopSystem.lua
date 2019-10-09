
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

LoopObject = Class(nil, "ALittle.LoopObject")

function LoopObject:IsCompleted()
end

function LoopObject:Completed()
end

function LoopObject:Update(frame_time)
end

function LoopObject:Reset()
end

function LoopObject:SetTime(time)
end

function LoopObject:SetCompleted()
end

function LoopObject:Start()
end

function LoopObject:Close()
end

LoopSystem = Class(nil, "ALittle.LoopSystem")

function LoopSystem:Ctor(weak)
	___rawset(self, "_loop_updaters", {})
	___rawset(self, "_in_update", false)
	___rawset(self, "_loop_cache", {})
	___rawset(self, "_cache_empty", true)
	if weak then
		Setweak(self._loop_updaters, true, false)
	end
end

function LoopSystem:AddUpdater(updater)
	if updater == nil then
		return
	end
	if self._in_update then
		self._cache_empty = false
		self._loop_cache[updater] = true
	else
		self._loop_updaters[updater] = true
	end
end

function LoopSystem:RemoveUpdater(updater)
	if updater == nil then
		return
	end
	if self._in_update then
		self._cache_empty = false
		self._loop_cache[updater] = false
	else
		self._loop_updaters[updater] = nil
	end
end

function LoopSystem:HasUpdater(updater)
	return self._loop_updaters[updater] ~= nil or self._loop_cache[updater] == true
end

function LoopSystem:Update(frame_time)
	self._in_update = true
	local remove_map = nil
	for updater, v in ___pairs(self._loop_updaters) do
		if updater:IsCompleted() then
			if remove_map == nil then
				remove_map = {}
			end
			remove_map[updater] = true
		else
			updater:Update(frame_time)
		end
	end
	if remove_map ~= nil then
		for updater, v in ___pairs(remove_map) do
			self._loop_updaters[updater] = nil
			updater:Completed()
		end
	end
	if not self._cache_empty then
		for updater, v in ___pairs(self._loop_cache) do
			if v then
				self._loop_updaters[updater] = true
			else
				self._loop_updaters[updater] = nil
			end
		end
		self._loop_cache = {}
		self._cache_empty = true
	end
	self._in_update = false
end

_G.A_LoopSystem = LoopSystem(false)
_G.A_WeakLoopSystem = LoopSystem(true)
