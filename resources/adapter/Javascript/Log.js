if (typeof ALittle == "undefined") ALittle = {};

// 设置打印的回调函数
ALittle.log = undefined;
function SetLogCallback(func)
{
    ALittle.log = func;
}

// 日志级别
ALittle.LogLevel =
{
    INFO : 0,
    WARN : 1,
    ERROR : 2,
}

// 日志函数
ALittle.Log = function()
{
    if (log == undefined)
        console.log(new_arg.join("\t"));
    else
        log(new_arg.join("\t"), 0);
}

// 日志函数
ALittle.Warn = function()
{
   if (log == undefined)
       console.warn(new_arg.join("\t"));
   else
       log(new_arg.join("\t"), 1);
}


// 日志函数
ALittle.Error = function()
{
   if (log == undefined)
       console.error(new_arg.join("\t"));
   else
       log(new_arg.join("\t"), 2);
}
