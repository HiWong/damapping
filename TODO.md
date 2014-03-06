 - TODO or not TODO: InstantiationType.SPRING_COMPONENT ?
        Ça hardcode une dépendance avec Spring.
        Ce serait sans doute mieux dans un module à part (qui s'occuperait aussi de générer SpringMapperContext.java).
        corrolaire : l'utilisation d'une enum pour indiquer la manière d'instancier le mapper n'est pas extensible
                     (ie. ajout d'un support Guice, JEE, ...) utiliser plutôt une interface ?
        meilleure idée
           => supprimer le paramètre de @Mapper
           => déduire le cas enum si c'est une enum qui est annotée
               le nom de la valeur de l'enum n'a pas d'importance, on valide juste à la compilation qu'il n'y en a qu'une
           => déduire le cas constructeur que c'est une enum qui est annotée
           => une API permet d'ajouter une annotation en plus de @Mapper qui fera que l'on créera une classe avec un @Component
              et une autre implémentation de factory
 - TODO : ajouter un paramètre à @Mapper pour préciser la visibilité de la classe Mapper générée (public par défaut, sinon protected)
           -> on n'ajoute pas de paramètre, la visibilité de l'interface générée sera la même que celle de la classe annotée avec @Mapper
 - framework d'injection super léger : essayer Dagger


ROADMAP
[X] intégration avec Spring, générer des classes annotées @Component pour les classes @Mapper elles-mêmes annotées avec @Mapper
[X] écriture d'un cas d'intégration avec un Spring contexte
[X] auto discovery de l'annotation processor
[X] séparation en modules spécifiques des annotations et du processor
[X] toute exception doit indiquer la classe @Mapper pour laquelle ça a pété
[ ] supprimer usage de FluentIterable.toImmutableList() et toImmutableSet() ou de toList() et toSet()
[ ] mettre en place un système d'exception internes pour ne pas faire des getMessager().[...] un peu partout, afficher
  ces exceptions avec un getMessager() mais ne pas remonter à javac
[ ] disposer d'une liste de String pour y coller les simples warnings pour ne pas faire de getMessager() un peu partout,
  afficher ces warnings avant de traiter l'exception interne
[ ] tester MapperFactory avec des Objets non java.lang, des génériques, ...
[ ] test U pour les méthodes appendType, appendParams, ... de DAStatementWriter ?
[ ] ajouter un contrôle : les MapperFactoryMethods doivent retourner le type de l'objet annotée avec @Mapper
[ ] ajouter une méthode appendReturn à DAStatementWriter ?
[ ] ajouter un test U pour DAType.superBound
[ ] ajout support de génériques avec bounds multiples : <T extends B1 & B2 & B3>
[ ] remplacer la propriété DAMethod.mapperFactoryMethod par l'ajout de la liste des annotations sur la méhode de sorte
  que la logique de calcul de cette propriété ne soit pas dupliquée dans le parsing Psi ou Javax
[ ] remplacer la propriété instantiationType dans DASourceClass en ajoutant les annotations au modèles
[ ] test U pour DAClassWriter.newClass
[X] reduire duplication dans le calcul des imports (ImportVisitable implemenations)
[ ] déplacer DAFileWriter#PackagePredicate dans une classe DAName predicate et ajouter des tests U
  (en particulier pour le package par défaut, ie. pas de package)
[ ] factoriser la génération des méthodes @MapperMethod (pour l'instant juste apply de Guava.Function) entre
  MapperImplFileGenerator et MapperFactoryImplFileGenerator ?
[X] ajouter les @Override manquant dans les classes générées Mapper*Impl
[ ] traiter les TODO et FIXME qui trainent dans les sources
[ ] supprimer les propriétés publiques dans les beans utilisés dans la genération de fichiers sources
[ ] ajouter test U pour les méthodes de DAMethod
[ ] ajouter test U pour DAMethodPredicates
[ ] passe de refacto : sortir code de lecture de API javac des annotation processors, predicates singleton si applicable, ajouter des tests U
[ ] étudier l'intégration avec l'IDE
[ ] visibilité du mapper généré
[ ] rendre optionel la dépendance à Spring
[ ] ajouter une annotation pour définir ou surcharger le comportement par défaut qui ajoute des @Component
[ ] ajouter une annotation @MapperMethod pour ne pas être dépendant de guava
[ ] removing model dependency on Javax.lang
    [X] DASourceClass#classElement
        [X] add enum flag to DASourceClass
        [X] add enum value(s) to DASourceClass
    [X] Modifier enum (DASourceClass, DAParameter, DAMethod)
    [X} remove ElementKind and TypeKind dependency