import os

# cwd is settings. determine project path
cwd = os.path.dirname(os.path.abspath(__file__)) 
project_path = cwd[:-9] # chop off "settings/"

DEBUG = True
TEMPLATE_DEBUG = DEBUG

TEMPLATE_DIRS = (
    # Put strings here, like "/home/html/django_templates" or "C:/www/django/templates".
    # Always use forward slashes, even on Windows.
    # Don't forget to use absolute paths, not relative paths.

    # automatically inject project templates directory into the dev env
    '%s/templates' % ( project_path )
)
