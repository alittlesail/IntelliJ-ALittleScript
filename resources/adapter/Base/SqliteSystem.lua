
module("ALittle", package.seeall)

local ___rawset = rawset
local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

SqliteDBStmt = Class(nil, "ALittle.SqliteDBStmt")

function SqliteDBStmt:Ctor(stmt)
	___rawset(self, "_stmt", stmt)
	___rawset(self, "_nrows", self._stmt:nrows())
end

function SqliteDBStmt:HasNextRow()
	if self._nrows == nil then
		return false
	end
	local key, row = next(self._nrows, self._key)
	if row ~= nil then
		self._key = key
		self._row = row
		return true
	end
	self:Reset()
	return false
end

function SqliteDBStmt:GetNextRow()
	return self._row
end

function SqliteDBStmt:Reset()
	self._stmt:reset()
	self._key = nil
	self._nrows = nil
	self._row = nil
end

function SqliteDBStmt:BindValues(...)
	self._stmt:bind_values(...)
end

SqliteDBObject = Class(nil, "ALittle.SqliteDBObject")

function SqliteDBObject:Ctor(db)
	___rawset(self, "_db", db)
end

function SqliteDBObject:Prepare(sql)
	local stmt = self._db:prepare(sql)
	if stmt == nil then
		return nil
	end
	return SqliteDBStmt(stmt)
end

function SqliteDBObject:Close()
	self._db:close()
end

function SqliteDBObject:Exec(sql)
	self._db:exec(sql)
end

SqliteDB = Class(nil, "ALittle.SqliteDB")

function SqliteDB.Open(db_path)
	local db = Sqlite3.open(db_path)
	if db == nil then
		return nil
	end
	return SqliteDBObject(db)
end

