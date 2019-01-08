import argparse
from sys import platform, argv

from sonicwall_rules import rules
from sonicwall_rules.tools import Tagger, read_exp_file_content
from sonicwall_rules.writers import FileWriter, TextWriter, HtmlWriter


description = \
'''
Generates tables from EXP backup files. These tables list rules which are considered threatening.
An old file is a file that was generated before a new one.
Than, those two files are compared.
'''
example_text = \
'''
example:

python {} -w /home/anuser/working_dir -f txt institution_2018-01-01_10.16.0.1.exp institution_2018-02-01_10.16.0.1.exp
'''.format(argv[0])

parser = argparse.ArgumentParser(description=description, epilog=example_text, formatter_class=argparse.RawTextHelpFormatter)
parser.add_argument('-w', '--work-dir', type=str,
                    default='.', help='working directory. Files will be read and written here')
parser.add_argument('-f', '--format', type=str, choices=['txt', 'html'],
                    default='txt', help="output file's format")
parser.add_argument('--title', type=str,
                    default='Rules report', help='page title. This option only applies on html output file')
parser.add_argument('old', type=str, help='path to an old backup file')
parser.add_argument('new', type=str, help='path to a new backup file')
args = parser.parse_args()

work_dir = args.work_dir
path_div = '\\' if platform.startswith('win') else '/'

if work_dir == '.':
    file_path = args.old
else:
    file_path = work_dir + path_div + args.old

conf_kv = read_exp_file_content(file_path)
old_rules = Tagger.categorize_rules(conf_kv)

print('access len =', len(old_rules['access_rules']))
print('nat len =', len(old_rules['nat_rules']))
print('total =', old_rules['total'])

if work_dir == '.':
    file_path = args.new
else:
    file_path = work_dir + path_div + args.new

conf_kv = read_exp_file_content(file_path)
new_rules = Tagger.categorize_rules(conf_kv)

print('access len =', len(new_rules['access_rules']))
print('nat len =', len(new_rules['nat_rules']))
print('total =', new_rules['total'])

analised_properties = [
    'SrcZone', 'DstZone', 'SrcNet', 'DstNet', 'DstSvc', 'Whom', 'Action', 'Enabled',
    'SrcZoneV6', 'DstZoneV6', 'SrcNetV6', 'DstNetV6', 'DstSvcV6', 'WhomV6', 'ActionV6', 'EnabledV6'
]
diff_rules = Tagger.diff_rules(old_rules, new_rules, analised_properties, 'access_rules')

if args.format == 'html':
    writer = HtmlWriter(work_dir + path_div + 'diff_access.html')
    writer.set_title(args.title)
else:
    writer = TextWriter(work_dir + path_div + 'diff_access.txt')

writer.append('########################################################', FileWriter.LINE)
writer.append('                   ACCESS RULES - IPv4', FileWriter.LINE)
writer.append('########################################################', FileWriter.LINE)

headers = ['#', 'ID', 'From', 'To', 'Source', 'Destination', 'Service', 'User Incl.', 'Action', 'Enabled']
table = list()
idx = 1
#rule_types = ['removed', 'added', 'unchanged']
rule_types = ['added', 'unchanged']

table.append(headers)
for r_type in rule_types:
    for rule in diff_rules[r_type]:
        if rule.protocol_version == rules.AccessRule.IPV4:
            rule_prop = rule.get_properties()
            rule_to_analyse = [
                idx,
                rule.rule_id,
                ('Any' if rule_prop['SrcZone'] == '' else rule_prop['SrcZone']),
                ('Any' if rule_prop['DstZone'] == '' else rule_prop['DstZone']),
                ('Any' if rule_prop['SrcNet'] == '' else rule_prop['SrcNet']),
                ('Any' if rule_prop['DstNet'] == '' else rule_prop['DstNet']),
                ('Any' if rule_prop['DstSvc'] == '' else rule_prop['DstSvc']),
                ('All' if rule_prop['Whom'] == '0' else rule_prop['Whom']),
                rule.get_action(),
                rule.get_enable()
            ]
            if Tagger.is_threatening_rule(rule_to_analyse):
                if args.format == 'html':
                    rule_to_analyse[1] = '<span style="color: red;">' + rule_to_analyse[1] + '</span>'
                table.append(rule_to_analyse)
                idx += 1

for rule in diff_rules['changed']:
    if rule[0].protocol_version == rules.AccessRule.IPV4:
        old_rule_prop = rule[0].get_properties()
        new_rule_prop = rule[1].get_properties()
        rule_to_analyse = [
            idx,
            rule[0].rule_id,
            ('Any' if new_rule_prop['SrcZone'] == '' else new_rule_prop['SrcZone']),
            ('Any' if new_rule_prop['DstZone'] == '' else new_rule_prop['DstZone']),
            ('Any' if new_rule_prop['SrcNet'] == '' else new_rule_prop['SrcNet']),
            ('Any' if new_rule_prop['DstNet'] == '' else new_rule_prop['DstNet']),
            ('Any' if new_rule_prop['DstSvc'] == '' else new_rule_prop['DstSvc']),
            ('All' if old_rule_prop['Whom'] == '0' else old_rule_prop['Whom'])
                + ' => ' + ('All' if new_rule_prop['Whom'] == '0' else new_rule_prop['Whom']),
            rule[1].get_action(),
            rule[1].get_enable()
        ]
        rule_id = rule[0].rule_id
        if Tagger.is_threatening_rule(rule_to_analyse):
            if args.format == 'html':
                rule_to_analyse[1] = '<span style="color: red;">' + rule_to_analyse[1] + '</span>'
            table.append(rule_to_analyse)
            idx += 1

writer.append(table.copy(), FileWriter.TABLE)
writer.append(writer.NEWLINE, FileWriter.LINE)

writer.append('########################################################', FileWriter.LINE)
writer.append('                   ACCESS RULES - IPv6', FileWriter.LINE)
writer.append('########################################################', FileWriter.LINE)

table.clear()
table.append(headers)
idx = 1
for r_type in rule_types:
    for rule in diff_rules[r_type]:
        if rule.protocol_version == rules.AccessRule.IPV6:
            rule_prop = rule.get_properties()
            rule_to_analyse = [
                idx,
                rule.rule_id,
                ('Any' if rule_prop['SrcZoneV6'] == '' else rule_prop['SrcZoneV6']),
                ('Any' if rule_prop['DstZoneV6'] == '' else rule_prop['DstZoneV6']),
                ('Any' if rule_prop['SrcNetV6'] == '' else rule_prop['SrcNetV6']),
                ('Any' if rule_prop['DstNetV6'] == '' else rule_prop['DstNetV6']),
                ('Any' if rule_prop['DstSvcV6'] == '' else rule_prop['DstSvcV6']),
                ('All' if rule_prop['WhomV6'] == '0' else rule_prop['WhomV6']),
                rule.get_action(),
                rule.get_enable()
            ]
            if Tagger.is_threatening_rule(rule_to_analyse):
                if args.format == 'html':
                    rule_to_analyse[1] = '<span style="color: red;">' + rule_to_analyse[1] + '</span>'
                table.append(rule_to_analyse)
                idx += 1

for rule in diff_rules['changed']:
    if rule[0].protocol_version == rules.AccessRule.IPV6:
        old_rule_prop = rule[0].get_properties()
        new_rule_prop = rule[1].get_properties()
        rule_to_analyse = [
            idx,
            rule[0].rule_id,
            ('Any' if new_rule_prop['SrcZoneV6'] == '' else new_rule_prop['SrcZoneV6']),
            ('Any' if new_rule_prop['DstZoneV6'] == '' else new_rule_prop['DstZoneV6']),
            ('Any' if new_rule_prop['SrcNetV6'] == '' else new_rule_prop['SrcNetV6']),
            ('Any' if new_rule_prop['DstNetV6'] == '' else new_rule_prop['DstNetV6']),
            ('Any' if new_rule_prop['DstSvcV6'] == '' else new_rule_prop['DstSvcV6']),
            ('All' if old_rule_prop['WhomV6'] == '0' else old_rule_prop['WhomV6'])
                + ' => ' + ('All' if new_rule_prop['WhomV6'] == '0' else new_rule_prop['WhomV6']),
            rule[1].get_action(),
            rule[1].get_enable()
        ]
        rule_id = rule[0].rule_id
        if Tagger.is_threatening_rule(rule_to_analyse):
            if args.format == 'html':
                rule_to_analyse[1] = '<span style="color: red;">' + rule_to_analyse[1] + '</span>'
            table.append(rule_to_analyse)
            idx += 1

writer.append(table.copy(), FileWriter.TABLE)
writer.append(writer.NEWLINE, FileWriter.LINE)

writer.build()
