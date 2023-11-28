import clang.cindex

clang.cindex.Config.set_library_path('/usr/lib/llvm-14/lib')
clang.cindex.Config.set_library_file('/usr/lib/llvm-14/lib/libclang-14.so')

def parse_function(code, function_name):
    index = clang.cindex.Index.create()
    translation_unit = index.parse("dummy.c", unsaved_files=[("dummy.c", code)])

    for node in translation_unit.cursor.get_children():
        if node.kind == clang.cindex.CursorKind.FUNCTION_DECL and node.spelling == function_name:
            arguments = [{'type': arg.type.spelling, 'name': arg.spelling} for arg in node.get_arguments()]
            return {
                'type': node.result_type.spelling,
                'arguments': arguments
            }

    return {'error': f'Function not found: {function_name}'}
