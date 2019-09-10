
module("ALittle", package.seeall)

local ___pairs = pairs
local ___ipairs = ipairs
local ___coroutine = coroutine

IFileLoader = Class(nil, "ALittle.IFileLoader")

function IFileLoader:Load(file_path)
end

assert(IFileLoader, " extends class:IFileLoader is nil")
NormalFileLoader = Class(IFileLoader, "ALittle.NormalFileLoader")

function NormalFileLoader:Load(file_path)
	local file = io.open(file_path, "r")
	if file == nil then
		return nil
	end
	local content = file:read("*a")
	file:close()
	return content
end

IFileSaver = Class(nil, "ALittle.IFileSaver")

function IFileSaver:Save(file_path, content)
end

assert(IFileSaver, " extends class:IFileSaver is nil")
NormalFileSaver = Class(IFileSaver, "ALittle.NormalFileSaver")

function NormalFileSaver:Save(file_path, content)
	local file = io.open(file_path, "w")
	if file == nil then
		return false
	end
	file:write(content)
	file:close()
	return true
end

function File_GetCurrentPath()
	return lfs.currentdir()
end

function File_SetCurrentPath(path)
	return lfs.chdir(path)
end

function File_RenameFile(path, new_path)
	return os.rename(path, new_path)
end

function File_DeleteFile(path)
	return os.remove(path)
end

function File_GetFileAttr(path)
	return lfs.attributes(path)
end

function File_IteratorDir(path)
	return lfs.dir(path)
end

function File_GetFileAttrByDir(path, file_map)
	if file_map == nil then
		file_map = {}
	end
	for file in lfs.dir(path) do
		if file ~= "." and file ~= ".." then
			local file_path = path .. "/" .. file
			local attr = lfs.attributes(file_path)
			if attr.mode == "directory" then
				File_GetFileAttrByDir(file_path, file_map)
			else
				file_map[file_path] = attr
			end
		end
	end
	return file_map
end

function File_DeleteDir(path)
	return lfs.rmdir(path)
end

function File_DeleteDeepDir(path, log_path)
	if path == nil or path == "" then
		return
	end
	if File_GetFileAttr(path) == nil then
		return
	end
	for file in lfs.dir(path) do
		if file ~= "." and file ~= ".." then
			local file_path = path .. "/" .. file
			local attr = lfs.attributes(file_path)
			if attr.mode == "directory" then
				File_DeleteDeepDir(file_path, log_path)
			else
				File_DeleteFile(file_path)
				if log_path then
					Log("delete file:", file_path)
				end
			end
		end
	end
	File_DeleteDir(path)
end

function File_MakeDir(path)
	return lfs.mkdir(path)
end

function File_MakeDeepDir(path)
	local path_list = String_SplitSepList(path, {"/", "\\"})
	local cur_path = ""
	for index, sub_path in ___ipairs(path_list) do
		cur_path = cur_path .. sub_path
		lfs.mkdir(cur_path)
		cur_path = cur_path .. "/"
	end
end

function File_GetFileNameByPath(file_path)
	local list = String_SplitSepList(file_path, {"/", "\\"})
	local len = table.maxn(list)
	if len <= 0 then
		return file_path
	end
	return list[len]
end

function File_GetFilePathByPath(file_path)
	local new_file_path = File_GetFileNameByPath(file_path)
	return String.sub(file_path, 1, -String.len(new_file_path) - 2)
end

function File_GetFileExtByPath(file_path)
	local list = String_Split(file_path, ".")
	local len = table.maxn(list)
	if len <= 0 then
		return file_path
	end
	return list[len]
end

function File_GetFileExtByPathAndUpper(file_path)
	return String.upper(File_GetFileExtByPath(file_path))
end

function File_GetJustFileNameByPath(file_path)
	local new_file_path = File_GetFileNameByPath(file_path)
	local list = String_Split(new_file_path, ".")
	local len = table.maxn(list)
	if len <= 1 then
		return new_file_path
	end
	return list[len - 1]
end

