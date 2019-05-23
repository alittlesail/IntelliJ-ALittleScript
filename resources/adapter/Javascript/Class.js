if (typeof ALittle == "undefined") ALittle = {};

if (typeof(Object.getOwnPropertyDescriptor) != "function")
	alert("您当前浏览器太旧，不支持Object.getOwnPropertyDescriptor，请使用360浏览器极速模式");
else if (typeof(Object.defineProperty) != "function")
	alert("您当前浏览器太旧，不支持Object.defineProperty，请使用360浏览器极速模式");

ALittle.ClassCtor = function(object, child_class, args)
{
	let super_class = child_class.__super;
	if (super_class) ALittle.ClassCtor(object, super_class, args);

	let ctor = child_class.Ctor;
	if (ctor) ctor.apply(object, args);
}

ALittle.__classex_map = {}
ALittle.__instance_map = {}

// 这个类是在ALittle.Class的基础上加强的
// 因为js文件一般都是异步加载，并且无法知道js文件之间类的继承关联
// ALittle.ClassEx有延迟加载的效果，可以一起不分先后加载js文件，及时这些js文件有类的继承依赖
// 这个函数可以保证在所有js全部加载完毕之后，类的继承都可以正确的执行。
// param class_name 类名，字符串，格式为 包名1.包名2.类名 比如 CDPWeb.CDPPlugin.Login
// param super_class_name 类名，字符串，格式为 包名1.包名2.类名 比如 CDPWeb.CDPPlugin.CommonDialog
// param child_class_prop 属性对象
// param static_function_prop 静态函数的属性
// return 没有返回值
ALittle.ClassEx = function(class_name, super_class_name, child_class_prop, static_function_prop)
{
	// 根据类名获取类所在的包
	let class_name_list = class_name.split(".");
	let package_object = window;
	for (let i = 0; i < class_name_list.length - 1; ++i)
	{
		let sub_package_object = package_object[class_name_list[i]];
		if (!sub_package_object)
		{
			sub_package_object = {};
			package_object[class_name_list[i]] = sub_package_object;
		}
		package_object = sub_package_object;
	}

	// 根据父类名获取类对象
	let super_class_object = null;
	if (super_class_name)
	{
		super_class_object = window;
		let super_class_name_list = super_class_name.split(".");
		for (let i = 0; i < super_class_name_list.length; ++i)
		{
			super_class_object = super_class_object[super_class_name_list[i]];
			if (!super_class_object) { super_class_object = null; break; }
		}
	}

	// 如果有填写父类，并且父类不存在，那么就延迟调用
	if (super_class_name && !super_class_object)
	{
		let map = ALittle.__classex_map[super_class_name];
		if (!map)
		{
			map = {}
			ALittle.__classex_map[super_class_name] = map;
		}

		let info = map[class_name];
		if (!info)
		{
			info = {};
			map[class_name] = info;
		}

		info.class_name = class_name;
		info.super_class_name = super_class_name;
		info.child_class_prop = child_class_prop;
		info.static_function_prop = static_function_prop;
	}
	// 如果存在那么就直接调用
	else
	{
	    // 构建类对象
		let class_object = ALittle.Class(super_class_object, child_class_prop, static_function_prop, class_name);
		package_object[class_name_list[class_name_list.length-1]] = class_object;

		// 构建单例
		let list = ALittle.__instance_map[class_name];
		if (list)
		{
			for (let callback in list)
			    callback(class_object);
			delete ALittle.__instance_map[class_name];
		}

        // 处理集成他的类
		let map = ALittle.__classex_map[class_name];
		if (map)
		{
			for (let name in map)
			{
				let info = map[name];
				ALittle.ClassEx(info.class_name, info.super_class_name, info.child_class_prop, info.callback);
			}
			delete ALittle.__classex_map[class_name];
		}
	}
}

ALittle.Class = function(super_class, child_class_prop, static_function_prop, class_name)
{
	// 创建类对象
	let child_class = {};

	let setter_map = {};
	let getter_map = {};

	console.assert(typeof super_class != "undefined", "父类不存在，是不是父类没有加载进来, class_name:" + String(class_name));

	// 处理父类
	if (super_class != null)
	{
		super_class = super_class.prototype;

		// 保存父类对象
		child_class.__super = super_class;
		child_class.__name = class_name;

		// 遍历所有父类的属性和函数
		for (let name in super_class)
		{
			// 除了构造函数和super_class的父类对象不需要复制，其他都要复制
			if (name != "Ctor" && name != "__super" && name != "__getter" && name != "__setter")
			{
				// 复制对应的setter，getter函数
				let info = Object.getOwnPropertyDescriptor(super_class, name);
				if (info.set || info.get) Object.defineProperty(child_class, name, info);

				if (info.set) setter_map[name] = info.set;
				if (info.get) getter_map[name] = info.get;

				// 如果两个都没有，那么这个基本上就是函数了，那么就直接复制
				if (!info.get && !info.set)
					child_class[name] = super_class[name];
			}
		}
	}

	// 同理复制当前的属性和函数
	for (let name in child_class_prop)
	{
		// 复制对应的setter，getter函数
		let info = Object.getOwnPropertyDescriptor(child_class_prop, name);
		if (!info.get && !info.set)
		{
			child_class[name] = child_class_prop[name];
		}
		else if (info.get && !info.set)
		{
			getter_map[name] = info.get;
			if (setter_map[name])
				info.set = setter_map[name];

			Object.defineProperty(child_class, name, info);
		}
		else if (!info.get && info.set)
		{
			setter_map[name] = info.set;
			if (getter_map[name])
				info.get = getter_map[name];
			Object.defineProperty(child_class, name, info);
		}
		else
		{
			getter_map[name] = info.get;
			setter_map[name] = info.set;
			Object.defineProperty(child_class, name, info);
		}
	}
	child_class.__getter = getter_map;
	child_class.__setter = setter_map;

	// 定义New的函数，用于实例化对象，为子类添加新的方法New
	function class_func()
	{
		// 把自己作为类保存在对象中
		this.__class = child_class;
		// 调用构造函数
		ALittle.ClassCtor(this, child_class, arguments);
	}
	class_func.prototype = child_class;

	for (let name in static_function_prop)
	    class_func[name] = static_function_prop[name];

	return class_func;
}

ALittle.Instance = function(class_name, callback)
{
    // 查找对象
    let class_object = window;
    let class_name_list = class_name.split(".");
    for (let i = 0; i < class_name_list.length; ++i)
    {
        class_object = class_object[class_name_list[i]];
        if (!class_object) { class_object = null; break; }
    }

    // 如果类已定义，那么就直接构建并返回
    if (class_object) { callback(class_object); return; }

    // 添加到列表中
    let list = ALittle.__instance_map[class_name];
    if (!list)
    {
        list = [];
        ALittle.__instance_map[class_name] = list;
    }
    list.push(callback);
}

// 检查某个对象是否是这个类
ALittle.InstanceOf = function(object, clazz)
{
    if (object == null) return false;
    if (typeof object != "object") return false;
    return object.__class == clazz;
}

// 检查是否是类
ALittle.IsClass = function(object)
{
    if (object == null) return false;
    if (typeof object != "object") return false;
    return object.__class != null && object.__class != undefined;
}

// 创建对象
ALittle.NewObject = function(clazz, ...)
{
    return new clazz(...);
}

// 获取类名
ALittle.GetClassName = function(object)
{
    if (!ALittle.IsClass(object)) return null;
    return object.__class.__name;
}

// 获取类
ALittle.GetClass = function(object)
{
    return object.__class;
}
// 获取类的getter集合
ALittle.GetClassGetter = function(clazz)
{
    let list = [];
    for (let name in clazz.__getter)
        list.push(name);
    return list;
}
// 获取类的setter集合
ALittle.GetClassSetter = function(clazz)
{
    let list = [];
    for (let name in clazz.__setter)
        list.push(name);
    return list;
}

// 创建一张空表
ALittle.CreateTable = function()
{
    return new Map();
}

// 设置弱引用 (JavaScript不支持这个函数)
ALittle.SetWeak = function(object, key, value)
{
}

// 表尾部添加元素
ALittle.ListPush = function(object, value)
{
    object._data.push(value);
}

// 构建List类
function List(data)
{
    if (data) this._data = data;
    else this._data = ["0"];
}

List.prototype.set = function(index, value)
{
	this._data[index] = value;
}

List.prototype.get = function(index)
{
	return this._data[index];
}

List.prototype[Symbol.iterator] = function()
{
	let i = 0;
	let object = this;
	return {
		next : function()
		{
			++ i;
			let value = object._data[i];
			return {
				done : value == null || value == undefined,
				value : [i, value]
			}
		}
	}
}
