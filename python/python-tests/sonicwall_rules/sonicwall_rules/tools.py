import re
from base64 import b64decode
from .rules import AccessRule, NatRule


class Tagger:
    @staticmethod
    def categorize_rules(conf_input):
        rules_dict = {
            'access_rules': [],
            'nat_rules': [],
            'total': 0
        }

        access_rules = rules_dict['access_rules']
        nat_rules = rules_dict['nat_rules']
        new_access_policy = new_nat_policy = None
        current_access_id = current_nat_id = -1
        for line in conf_input:
            if re.match('^policy', line):
                policy_field, policy_id, policy_value = re.search('^policy(.*)_(\d+)=(.*)', line).groups()
                if policy_id != current_access_id:
                    if new_access_policy is not None:
                        access_rules.append(new_access_policy)
                        rules_dict['total'] += 1
                    protocol = AccessRule.IPV6 if policy_field.endswith('V6') else AccessRule.IPV4
                    new_access_policy = AccessRule(policy_id, protocol)
                    current_access_id = policy_id

                new_access_policy.set_property(policy_field, policy_value)

            elif re.match('^natPolicy', line):
                policy_field, policy_id, policy_value = re.search('^natPolicy(.*)_(\d+)=(.*)', line).groups()
                if policy_id != current_nat_id:
                    if new_nat_policy is not None:
                        nat_rules.append(new_nat_policy)
                        rules_dict['total'] += 1
                    protocol = NatRule.IPV6 if policy_field.endswith('V6') else NatRule.IPV4
                    new_nat_policy = NatRule(policy_id, protocol)
                    current_nat_id = policy_id

                new_nat_policy.set_property(policy_field, policy_value)

        return rules_dict

    @staticmethod
    def diff_rules(old_rules, new_rules, analised_properties=list(), rules_type='access_rules'):
        if rules_type != 'access_rules' and rules_type != 'nat_rules':
            return

        removed_rules = list()
        added_rules = list()
        unchanged_rules = list()
        changed_rules = list()

        for new_rule in new_rules[rules_type]:
            try:
                old_rules[rules_type].index(new_rule)
            except ValueError:
                added_rules.append(new_rule)

        for old_rule in old_rules[rules_type]:
            try:
                new_rule = new_rules[rules_type].index(old_rule)
                new_rule = new_rules[rules_type][new_rule]

                old_prop = old_rule.get_properties()
                new_prop = new_rule.get_properties()

                was_changed = False
                for prop_key, old_value in old_prop.items():
                    if old_value != new_prop[prop_key] and prop_key in analised_properties:
                        was_changed = True
                        break
                if was_changed:
                    changed_rules.append((old_rule, new_rule))
                else:
                    unchanged_rules.append(old_rule)
            except ValueError:
                removed_rules.append(old_rule)

        return {
            'removed': removed_rules,
            'added': added_rules,
            'unchanged': unchanged_rules,
            'changed': changed_rules
        }

    @staticmethod
    def is_threatening_rule(rule):
        frm = 2
        to = 3
        src = 4
        dst = 5
        svc = 6
        who = 7
        act = 8
        enb = 9
        any_count = 0

        if rule[src] == 'Any':
            any_count += 1
        if rule[dst] == 'Any':
            any_count += 1
        if rule[svc] == 'Any':
            any_count += 1

        return (any_count > 1 or (rule[who].endswith('=> All') and rule[who] != 'All => All'))\
            and rule[frm] != rule[to] and rule[act] == 'Allow'


def read_exp_file_content(file_path):
    with open(file_path, 'r') as in_file:
        content = in_file.read()
        content = str(b64decode(content))
        content = content.replace('%20', ' ')
        content = content.replace('%3a', ':')
        content = content.split('&')
    return content
