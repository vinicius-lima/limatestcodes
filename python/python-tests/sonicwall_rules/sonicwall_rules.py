import argparse
import datetime
from sys import platform, argv, exit

from sonicwall_rules import rules
from sonicwall_rules.tools import Tagger, read_exp_file_content, list_files
from sonicwall_rules.writers import FileWriter, TextWriter, HtmlWriter


description = \
'''
Generates tables from EXP backup files. These tables list rules which are considered threatening.
'''
example_text = \
'''
example:

python {} -w /home/anuser/working_dir -o /home/anuser/output_dir -f txt
'''.format(argv[0])

parser = argparse.ArgumentParser(description=description, epilog=example_text, formatter_class=argparse.RawTextHelpFormatter)
parser.add_argument('-w', '--work-dir', type=str,
                    default='.', help='working directory. Root path where backup files are read from')
parser.add_argument('-o', '--output-dir', type=str,
                    default='.', help='output directory. Output files are written here')
parser.add_argument('-f', '--format', type=str, choices=['txt', 'html'],
                    default='txt', help="output file's format")
args = parser.parse_args()

analised_properties = [
    'SrcZone', 'DstZone', 'SrcNet', 'DstNet', 'DstSvc', 'Whom', 'Action', 'Enabled',
    'SrcZoneV6', 'DstZoneV6', 'SrcNetV6', 'DstNetV6', 'DstSvcV6', 'WhomV6', 'ActionV6', 'EnabledV6'
]

work_dir = args.work_dir
path_div = '\\' if platform.startswith('win') else '/'
print('[!] Working directory:', work_dir)

today = datetime.date.today()
week_ago = datetime.timedelta(days=7)
week_ago = today - week_ago

today_path = '{0}{1}{2}'.format(work_dir, path_div, str(today))
week_ago_path = '{0}{1}{2}'.format(work_dir, path_div, str(week_ago))

print('[+] Searching backup files in', today_path)
backup_files = list_files('{0}{1}{2}{1}*.exp'.format(work_dir, path_div, str(today)))
print('[!]', len(backup_files), 'files found')

print('[+] Analysing backup files...')
for file_path in backup_files:
    path_tokens = file_path.split('_')
    prefix = path_tokens[0]
    host = prefix.split(path_div)[-1]
    complement = path_tokens[2]

    new_configuration_values = read_exp_file_content(file_path)
    new_rules = Tagger.categorize_rules(new_configuration_values)

    try:
        old_configuration_values = read_exp_file_content(week_ago_path + path_div + host + '_' + str(week_ago) + '_' + complement)
    except FileNotFoundError:
        print('[-] Could not find backup file for', host, 'in', week_ago_path)
        old_configuration_values = new_configuration_values
    old_rules = Tagger.categorize_rules(old_configuration_values)

    diff_rules = Tagger.diff_rules(old_rules, new_rules, analised_properties, 'access_rules')

    output_path = args.output_dir + path_div + host + '_' + str(today) + '_' + complement
    if args.format == 'html':
        output_path = output_path[:-4] + '.html'
        writer = HtmlWriter(output_path)
        writer.set_title(host)
    else:
        output_path = output_path[:-4] + '.txt'
        writer = TextWriter(output_path)

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

print('[!] DONE. Ouput files are stored in', args.output_dir, 'as', args.format)
