#!/usr/bin/env bash
set -e

PLANTUML_JAR="scripts/plantuml.jar"
UML_DIR="docs/uml"
mkdir -p "$UML_DIR"

CHANGED_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.java$' || true)

if [ -z "$CHANGED_FILES" ]; then
  exit 0
fi

PUML_FILE="$UML_DIR/classes.puml"

{
  echo "@startuml"
  echo "skinparam classAttributeIconSize 0"
  echo ""

  for FILE in $CHANGED_FILES; do
    [ -f "$FILE" ] || continue
    CLASSNAME=$(grep -m1 'public class\|public interface\|public abstract class' "$FILE" \
      | sed 's/.*\(class\|interface\) \([A-Za-z0-9_]*\).*/\2/')
    PACKAGE=$(grep -m1 '^package ' "$FILE" \
      | sed 's/package \(.*\);/\1/')

    [ -z "$CLASSNAME" ] && continue

    if grep -q 'interface ' "$FILE"; then
      echo "interface $CLASSNAME {"
    elif grep -q 'abstract class ' "$FILE"; then
      echo "abstract class $CLASSNAME {"
    else
      echo "class $CLASSNAME {"
    fi

    grep -E '^\s+(private|public|protected)\s+\w+\s+\w+\s*;' "$FILE" \
      | sed 's/^\s*/  /' || true

    grep -E '^\s+public\s+\w[\w<>, ]*\s+\w+\s*\(' "$FILE" \
      | sed 's/^\s*/  /' \
      | sed 's/{.*//' || true

    echo "}"
    echo ""
  done

  echo "@enduml"
} > "$PUML_FILE"

java -jar "$PLANTUML_JAR" "$PUML_FILE" -o "$(pwd)/$UML_DIR"

git add "$UML_DIR/classes.puml" "$UML_DIR/classes.png" 2>/dev/null || true

echo "UML completed"