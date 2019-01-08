from abc import ABC
from json import dumps


class Rule(ABC):
    IPV4 = '4'
    IPV6 = '6'

    def __init__(self, rule_id, protocol_version):
        self.rule_id = rule_id
        self._properties = dict()
        self.protocol_version = protocol_version
        self.set_property('ProtocolVersion', self.protocol_version)

    def __eq__(self, other):
        return self.rule_id == other.rule_id and self.protocol_version == other.protocol_version

    def __ne__(self, other):
        return self.rule_id != other.rule_id or self.protocol_version != other.protocol_version

    def __lt__(self, other):
        return self.rule_id - other.rule_id < 0

    def __str__(self):
        return dumps({self.rule_id: self._properties})

    def pretty_print(self, indent=2):
        return dumps({self.rule_id: self._properties}, indent=indent)

    def set_property(self, rule_property, value):
        self._properties[rule_property] = value

    def get_properties(self):
        return self._properties.copy()

    def get_enable(self):
        enabled = self._properties.get('Enabled')
        if enabled is None:
            enabled = self._properties.get('EnabledV6', '0')

        switcher = {
            "0": "No",
            "1": "Yes"
        }
        return switcher.get(enabled, "No")


class AccessRule(Rule):
    def __init__(self, rule_id, protocol_version):
        super().__init__(rule_id, protocol_version)

    def get_action(self):
        action = self._properties.get('Action')
        if action is None:
            action = self._properties.get('ActionV6')

        switcher = {
            "1": "Discard",
            "2": "Allow"
        }

        return switcher.get(action, "Deny")


class NatRule(Rule):
    def __init__(self, rule_id, protocol_version):
        super().__init__(rule_id, protocol_version)
