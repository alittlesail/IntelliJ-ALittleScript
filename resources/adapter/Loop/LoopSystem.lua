
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

LoopObject = Class(nil, "ALittle.LoopObject")

function LoopObject:IsCompleted()
	return true
end

function LoopObject:Completed()
end

function LoopObject:Update(frame_time)
end

function LoopObject:Reset()
end

function LoopObject:SetTime(time)
	return time, true
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
	___rawset(self, "_timer", TimerSystem())
	___rawset(self, "_handler_map", {})
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

function LoopSystem:AddTimer(delay_ms, callback, loop, interval_ms)
	if callback == nil then
		return 0
	end
	if loop == nil then
		loop = 1
	end
	if interval_ms == nil then
		interval_ms = 1
	end
	local id = self._timer:AddTimer(delay_ms, loop, interval_ms)
	self._handler_map[id] = callback
	return id
end

function LoopSystem:RemoveTimer(id)
	if id == nil then
		return false
	end
	self._handler_map[id] = nil
	return self._timer:RemoveTimer(id)
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
	self._timer:UpdateTime(frame_time)
	while true do
		local id = self._timer:Poll()
		if id == 0 then
			break
		end
		if id < 0 then
			local handle = self._handler_map[-id]
			if handle ~= nil then
				handle()
				self._handler_map[-id] = nil
			end
		else
			local handle = self._handler_map[id]
			if handle ~= nil then
				handle()
			end
		end
	end
end

_G.A_LoopSystem = LoopSystem(false)
_G.A_WeakLoopSystem = LoopSystem(true)
