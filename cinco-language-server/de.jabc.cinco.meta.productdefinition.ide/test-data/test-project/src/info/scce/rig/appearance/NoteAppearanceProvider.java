package info.scce.rig.appearance;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.jabc.cinco.meta.core.ge.style.generator.runtime.appearance.StyleAppearanceProvider;
import info.scce.rig.pipeline.Note;
import style.Appearance;
import style.Color;
import style.StyleFactory;

public class NoteAppearanceProvider implements StyleAppearanceProvider<Note> {

	@Override
	public Appearance getAppearance(Note note, String shape) {
		Appearance appearance = StyleFactory.eINSTANCE.createAppearance();
		switch (shape) {
			case "body":
				Color color = StyleFactory.eINSTANCE.createColor();
				List<Integer> rgb = Arrays.stream(note.getColor().split(","))
					.map(String::trim)
					.map(Integer::parseInt)
					.collect(Collectors.toList());
				color.setR(rgb.get(0));
				color.setG(rgb.get(1));
				color.setB(rgb.get(2));
				appearance.setBackground(color);
				break;
		}
		return appearance;
	}
}
