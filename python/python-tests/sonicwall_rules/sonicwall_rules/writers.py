from abc import ABC, abstractmethod
from tabulate import tabulate

from yattag import Doc, indent


class FileWriter(ABC):
    LINE = 'line'
    TABLE = 'table'
    NEWLINE = '\n'

    def __init__(self, file_path):
        self._content = list()
        self._file_path = file_path

    def append(self, element, element_type=LINE):
        self._content.append({'element': element, 'type': element_type})

    @abstractmethod
    def build(self):
        pass


class TextWriter(FileWriter):
    def __init__(self, file_path):
        super().__init__(file_path)

    def build(self):
        with open(self._file_path, 'w') as out_file:
            for element in self._content:
                if element['type'] == self.LINE:
                    out_file.write(element['element'] + '\n')
                elif element['type'] == self.TABLE:
                    out_file.write(tabulate(element['element'], tablefmt='grid'))
                    out_file.write('\n')


class HtmlWriter(FileWriter):
    NEWLINE = '<br>'

    def __init__(self, file_path):
        super().__init__(file_path)
        self.title = 'Page Title'

    def build(self):
        doc, tag, text, line = Doc().ttl()

        doc.asis('<!DOCTYPE html>')
        with tag('html'):
            with tag('head'):
                doc.stag('meta', charset="utf-8")
                with tag('script'):
                    doc.attr(src="https://code.jquery.com/jquery-3.3.1.slim.min.js")
                    doc.attr(integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo")
                    doc.attr(crossorigin="anonymous")
                with tag('script'):
                    doc.attr(src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js")
                    doc.attr(integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49")
                    doc.attr(crossorigin="anonymous")
                doc.stag('link',
                         rel="stylesheet",
                         href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css",
                         integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO",
                         crossorigin="anonymous")
                with tag('script'):
                    doc.attr(src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js")
                    doc.attr(integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy")
                    doc.attr(crossorigin="anonymous")
                line('title', self.title)
            with tag('body'):
                for element in self._content:
                    if element['type'] == self.LINE:
                        doc.asis('<p>' + element['element'] + '</p>')
                
                    elif element['type'] == self.TABLE:
                        table = element['element']
                        with tag('table', klass='table table-bordered table-striped'):
                            with tag('thead'):
                                with tag('tr'):
                                    for cell in table[0]:
                                        line('th', cell)
                            with tag('tbody'):
                                for row in range(1, len(table)):
                                    with tag('tr'):
                                        for cell in table[row]:
                                            c = str(cell)
                                            c = c.replace('=>', '=&gt;')
                                            doc.asis('<td>' + c + '</td>')

        result = indent(doc.getvalue())
        with open(self._file_path, 'w') as out_file:
            out_file.write(result)

    def set_title(self, title):
        self.title = title
