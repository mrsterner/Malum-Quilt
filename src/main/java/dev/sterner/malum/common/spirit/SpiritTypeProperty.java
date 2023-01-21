package dev.sterner.malum.common.spirit;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.state.property.Property;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpiritTypeProperty extends Property<String> {

	private final ImmutableSet<String> values;
	private final Map<String, MalumSpiritType> types = Maps.newHashMap();

	public SpiritTypeProperty(String name, Collection<MalumSpiritType> types) {
		super(name, String.class);
		this.values = ImmutableSet.copyOf(types.stream().map(s -> s.identifier).collect(Collectors.toList()));

		for (MalumSpiritType type : types) {
			if (this.types.containsKey(type.identifier)) {
				throw new IllegalArgumentException("Multiple values have the same name '" + type.identifier + "'");
			}

			this.types.put(type.identifier, type);
		}
	}

	@Override
	public Collection<String> getValues() {
		return this.values;
	}

	@Override
	public Optional<String> parse(String value) {
		return values.stream().filter(v -> v.equals(value)).findAny();
	}

	@Override
	public String name(String value) {
		return value;
	}

	@Override
	public boolean equals(Object pOther) {
		if (this == pOther) {
			return true;
		} else if (pOther instanceof SpiritTypeProperty property && super.equals(pOther)) {
			return this.values.equals(property.values) && this.types.equals(property.types);
		} else {
			return false;
		}
	}

	@Override
	public int computeHashCode() {
		int i = super.computeHashCode();
		i = 31 * i + this.values.hashCode();
		return 31 * i + this.types.hashCode();
	}
}
