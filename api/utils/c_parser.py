import clang.cindex

clang.cindex.Config.set_library_path('/usr/lib/llvm-14/lib')
clang.cindex.Config.set_library_file('/usr/lib/llvm-14/lib/libclang-14.so')

def parse_function(code, function_name):
    index = clang.cindex.Index.create()
    translation_unit = index.parse("dummy.c", unsaved_files=[("dummy.c", code)])

    for node in translation_unit.cursor.get_children():
        if node.kind == clang.cindex.CursorKind.FUNCTION_DECL and node.spelling == function_name:
            return {
                'type': node.result_type.spelling,
                'arguments': [(arg.spelling, arg.type.spelling) for arg in node.get_arguments()]
            }

    return {'error': f'Function not found: {function_name}'}





if __name__ == "__main__":
    source_code = """
    int;
    }
    """
    function_name = "add_numbers"
    result = parse_function(source_code, function_name)

    if 'error' in result:
        print(f"Error: {result['error']}")
    else:
        print(f"Function Type: {result['type']}")
        print("Arguments:")
        for arg_name, arg_type in result['arguments']:
            print(f"  {arg_type} {arg_name}")
