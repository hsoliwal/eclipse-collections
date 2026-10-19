# SPDX-License-Identifier: Apache-2.0
"""Pure, exact, reversible per-file overlay; callers own filesystem publication."""
import hashlib

def _sha(raw):
    return hashlib.sha256(raw).hexdigest()

def transform(before, postimages, recipe, reverse=False):
    rows = recipe['files']
    paths = [row['path'] for row in rows]
    if len(paths) != len(set(paths)) or paths != recipe['applicationOrder']:
        raise ValueError('recipe path order refused')
    if set(before) != set(paths) or set(postimages) != set(paths):
        raise ValueError('exact five-file input set required')
    result = {}
    for row in rows:
        path = row['path']
        post = postimages[path]
        if _sha(post) != row['afterSha256']:
            raise ValueError('postimage identity refused: ' + path)
        pre = None
        if row['beforeSha256'] is not None:
            pre = post.decode('utf-8')
            for old, new in reversed(recipe['ownerEdits']):
                if new:
                    pre = pre.replace(new, old)
                else:
                    anchor = 'import java.util.SortedMap;\n'
                    if pre.count(anchor) != 1:
                        raise ValueError('owner import anchor refused')
                    pre = pre.replace(anchor, anchor + old)
            pre = pre.encode('utf-8')
            if _sha(pre) != row['beforeSha256']:
                raise ValueError('owner inverse identity refused')
        source, target = (post, pre) if reverse else (pre, post)
        actual = before[path]
        if actual != source and actual != target:
            raise ValueError('unknown source state refused: ' + path)
        result[path] = target
    return result
