
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

EventDispatcher = Class(nil, "ALittle.EventDispatcher")

function EventDispatcher:Ctor()
	___rawset(self, "_listeners", {})
	___rawset(self, "_abs_disabled", false)
end

function EventDispatcher:AddEventListener(event_type, object, callback)
	if object == nil then
		return false
	end
	if callback == nil then
		return false
	end
	local callback_table = self._listeners[event_type]
	if callback_table == nil then
		callback_table = {}
		Setweak(callback_table, true, false)
		self._listeners[event_type] = callback_table
	end
	local callback_value = callback_table[object]
	if callback_value == nil then
		callback_value = {}
		callback_table[object] = callback_value
	end
	callback_value[callback] = true
	return true
end

function EventDispatcher:RemoveEventListener(event_type, object, callback)
	local callback_table = self._listeners[event_type]
	if callback_table == nil then
		return
	end
	if callback == nil then
		callback_table[object] = nil
	else
		local callback_value = callback_table[object]
		if callback_value == nil then
			return
		end
		callback_value[callback] = nil
	end
end

function EventDispatcher:ClearEventListener()
	self._listeners = {}
end

function EventDispatcher:DispatchEventType(type)
	local event = {}
	event.target = self
	event.type = type
	self:DispatchEvent(event)
end

function EventDispatcher:DispatchEvent(event)
	local callback_table = self._listeners[event.type]
	if callback_table == nil then
		return
	end
	for object, callback_value in ___pairs(callback_table) do
		for func, _ in ___pairs(callback_value) do
			func(object, event)
		end
	end
end

