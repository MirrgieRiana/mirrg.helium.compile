package mirrg.helium.compile.oxygen.apatite2;

import java.awt.Color;
import java.util.Optional;

import javax.swing.WindowConstants;

import mirrg.helium.compile.oxygen.apatite2.loader.Loader;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteCode;
import mirrg.helium.compile.oxygen.apatite2.node.IApatiteScript;
import mirrg.helium.compile.oxygen.apatite2.nodes.NodeFunction;
import mirrg.helium.compile.oxygen.apatite2.type.Type;
import mirrg.helium.compile.oxygen.apatite2.util.EventFrameApatite;
import mirrg.helium.compile.oxygen.apatite2.util.FrameApatite;
import mirrg.helium.swing.nitrogen.util.HColor;

public class Sample1
{

	public static void main(String[] args)
	{
		FrameApatite frame = new FrameApatite(ApatiteScript.getSyntax(), ""
			+ "\"[\" + atan2(10, -10) / 3.141592 + \", \" + sample + \"]\""
			+ "\n+ \"\\n\" + *&(20 + 30)"
			+ "\n+ \"\\n\" + depointer(pointer(false))"
			+ "\n+ \"\\n\" + depointer(pointer(keyword))"
			+ "\n+ \"\\n\" + type(pointer(keyword))") {

			@Override
			protected ApatiteVM createVM()
			{
				ApatiteVM vm = super.createVM();

				new Loader2(vm).load();

				return vm;
			}

		};
		frame.setTitle("Apatite Script");

		frame.event().register(EventFrameApatite.ChangeSource.class, e -> System.out.println(e.source));

		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private static class Loader2 extends Loader
	{

		protected Loader2(ApatiteVM vm)
		{
			super(vm);
		}

		@Override
		public void load()
		{
			rc("sample", I, -4142);
			rf2("atan2", D, D, D, Math::atan2);

			vm.registerMetaFunction(new IApatiteMetaFunctionProvider() {

				@Override
				public Optional<IApatiteMetaFunctionEntity> matches(IApatiteCode... codes)
				{
					if (codes.length != 1) return Optional.empty();

					return Optional.of(new IApatiteMetaFunctionEntity() {

						@Override
						public Optional<IApatiteScript> validate(int begin, int end, ApatiteVM vm)
						{
							Optional<IApatiteScript> script = codes[0].validate(vm);
							if (!script.isPresent()) return Optional.empty();

							TypePointer type = new TypePointer(script.get().getType());

							return Optional.of(new IApatiteScript() {

								@Override
								public mirrg.helium.compile.oxygen.apatite2.type.Type<?> getType()
								{
									return type;
								}

								@Override
								public Object invoke()
								{
									return script.get();
								}

							});
						}

					});
				}

			}, "pointer", "_leftAmpersand");

			vm.registerMetaFunction(new IApatiteMetaFunctionProvider() {

				@Override
				public Optional<IApatiteMetaFunctionEntity> matches(IApatiteCode... codes)
				{
					if (codes.length != 1) return Optional.empty();

					return Optional.of(new IApatiteMetaFunctionEntity() {

						@Override
						public Optional<IApatiteScript> validate(int begin, int end, ApatiteVM vm)
						{
							Optional<IApatiteScript> script = codes[0].validate(vm);
							if (!script.isPresent()) return Optional.empty();

							if (!(script.get().getType() instanceof TypePointer)) {
								vm.reportError(begin, end, "Only pointer can be depointered: " + script.get().getType());
								return Optional.empty();
							}

							return Optional.of(new IApatiteScript() {

								@Override
								public mirrg.helium.compile.oxygen.apatite2.type.Type<?> getType()
								{
									return ((TypePointer) script.get().getType()).parent;
								}

								@Override
								public Object invoke()
								{
									return ((IApatiteScript) script.get().invoke()).invoke();
								}

							});
						}

					});
				}

			}, "depointer", "_leftAsterisk");

			vm.registerMetaFunction(new IApatiteMetaFunctionProvider() {

				@Override
				public Optional<IApatiteMetaFunctionEntity> matches(IApatiteCode... codes)
				{

					return Optional.of(new IApatiteMetaFunctionEntity() {

						@Override
						public Optional<IApatiteScript> validate(int begin, int end, ApatiteVM vm)
						{
							return new NodeFunction("_leftAmpersand", begin, end, codes).validate(vm);
						}

					});
				}

			}, "tree");

		}

	}

	private static class TypePointer extends Type<Object>
	{

		public final Type<?> parent;

		public TypePointer(Type<?> parent)
		{
			super("pointer", HColor.createLinearRatioColor(0.5, parent.color, Color.decode("#000000")));
			this.parent = parent;

			registerDistance(Loader.VALUE, 1, a -> a);
		}

		@Override
		public String getName()
		{
			return super.getName() + "(" + parent.getName() + ")";
		}

	}

}
