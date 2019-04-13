if (typeof ALittle == "undefined") ALittle = {};

// 函数委托
// func 函数对象
// object 类对象
ALittle.Bind = function(func, object)
{
	if(!func)console.error(func, object)
	console.assert(typeof func == "function", "func is not a function:" + typeof func);
	console.assert(typeof object == "object", "object is not a object:" + typeof object);
	// 如果参数小于2个，那么直接返回
	if (arguments.length < 2) return null;

	// 保存所有参数
	let functor_arguments = arguments;
	
	return function ()
	{
		// 重新构建参数列表，从索引2开始的参数为携带的参数
		// functor_arguments 创建Functor对象时携带的参数
		let arg_list = [];
		let arguments_len = functor_arguments.length;
		for (let i = 2; i < arguments_len; ++i)
			arg_list.push(functor_arguments[i]);

		// 把后面的参数添加进去
		// arguments 执行实例时的参数
		arguments_len = arguments.length;
		for (let i = 0; i < arguments_len; ++i)
			arg_list.push(arguments[i]);

		// 调用委托函数
		return func.apply(object, arg_list);
	}
}
