from javalang import tree

def parse_java_code(code):
    try:
        parser = tree.Parser()
        parsed_code = parser.parse(code)

        return_type = None
        function_name = None
        arguments = []

        for path, node in parsed_code:
            if isinstance(node, tree.MethodDeclaration):
                return_type = node.return_type.name
                function_name = node.name

                for parameter in node.parameters:
                    arguments.append(parameter.type.name)

        return return_type, function_name, arguments

    except Exception as e:
        print(f"Error while parsing Java code: {str(e)}")
        return None, None, []