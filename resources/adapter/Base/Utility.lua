
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

local __lshift = bit.lshift
local __rshift = bit.rshift
local __bxor = bit.bxor
local __byte = String.byte
local __sub = String.sub
local __len = String.len
function JSHash(str)
	local l = __len(str)
	local h = l
	local step = __rshift(l, 5) + 1
	for i = l, step, -step do
		h = __bxor(h, (__lshift(h, 5) + __byte(str, i) + __rshift(h, 2)))
	end
	return h
end

