package fr.phan.damapping.processor.impl;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

/**
 * MapperFileGenerator -
 *
 * @author Sébastien Lesaint
 */
class MapperFileGenerator extends AbstractFileGenerator {
    @Override
    public String fileName(FileGeneratorContext context) {
        return context.getSourceClass().type.qualifiedName.getName() + "Mapper";
    }

    @Override
    public void writeFile(BufferedWriter bw, FileGeneratorContext context) throws IOException {

        // générer l'interface du Mapper
        //     -> nom de package
        //     -> nom de la classe (infère nom du Mapper)
        //     -> visibilite de la classe (protected ou public ?)
        //     -> liste des interfaces implémentées
        //     -> compute liste des imports à réaliser
        DASourceClass sourceClass = context.getSourceClass();
        DAFileWriter fileWriter = new DAFileWriter(bw)
                .appendPackage(sourceClass.packageName)
                .appendImports(context.getMapperImports())
                .appendWarningComment();

        fileWriter.newInterface(sourceClass.type.simpleName + "Mapper")
                .withModifiers(filterModifiers(sourceClass.modifiers))
                .withExtended(toDAType(sourceClass.interfaces)).start().end();

        bw.flush();
        bw.close();
    }

    private static List<DAType> toDAType(List<DAInterface> interfaces) {
        return FluentIterable.from(interfaces)
                .transform(new Function<DAInterface, DAType>() {
                    @Nullable
                    @Override
                    public DAType apply(@Nullable DAInterface daInterface) {
                        if (daInterface == null) {
                            return null;
                        }
                        return daInterface.type;
                    }
                }).filter(Predicates.notNull())
                .toList();
    }

    private static Set<Modifier> filterModifiers(Set<Modifier> modifiers) {
        return FluentIterable.from(modifiers)
                .filter(
                        Predicates.not(
                                // an interface can not be final, will not compile
                                Predicates.equalTo(Modifier.FINAL)
                        )
                )
                .toSet();
    }
}